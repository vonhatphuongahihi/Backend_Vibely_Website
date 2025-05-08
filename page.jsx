"use client";

import * as React from 'react';
import {
    Day, Week, Month, Agenda, ScheduleComponent, Inject,
    ViewsDirective, ViewDirective
} from '@syncfusion/ej2-react-schedule';
import { useEffect, useState } from 'react';
import "@syncfusion/ej2-base/styles/material.css";
import "@syncfusion/ej2-react-schedule/styles/material.css";
import { registerLicense } from '@syncfusion/ej2-base';
import axios from "axios";
import './schedule.css';

registerLicense('Ngo9BigBOggjHTQxAR8/V1NNaF5cXmBCe0x3WmFZfVtgdl9DZVZURWYuP1ZhSXxWdkFjWH9cdXFQQ2ZZU0x9XUs=');

const Schedule = () => {
    const [events, setEvents] = useState([]);
    const API_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8081';


    useEffect(() => {
        const fetchSchedules = async () => {
            try {
                const token = localStorage.getItem("token");
                if (!token) {
                    console.error("❌ Không tìm thấy token");
                    return;
                }

                const response = await fetch(`${API_URL}/schedules`, {
                    headers: { Authorization: `Bearer ${token}` }
                });

                if (!response.ok) throw new Error("Lỗi lấy dữ liệu");

                const result = await response.json();

                const data = Array.isArray(result.data) ? result.data : [];
                const formattedData = data.map((item) => ({
                    Id: item._id,
                    Subject: item.subject,
                    StartTime: new Date(item.startTime),
                    EndTime: new Date(item.endTime),
                    CategoryColor: item.categoryColor || "#00FF00",
                    isAllDay: item.isAllDay || false
                }));


                setEvents(formattedData);
            } catch (error) {
                console.error("❌ Lỗi khi lấy lịch trình:", error);
            }
        };


        fetchSchedules();
    }, []);
    const addEvent = async (event) => {
        try {
            const token = localStorage.getItem("token");
            if (!token) {
                console.error("❌ Không tìm thấy token");
                return;
            }

            const response = await fetch(`${API_URL}/schedules`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    subject: event.Subject,
                    startTime: event.StartTime,
                    endTime: event.EndTime,
                    categoryColor: event.CategoryColor || "#0000FF"
                })
            });

            const result = await response.json();
            if (!response.ok) throw new Error(result.message);

            setEvents([...events, { ...event, Id: result.data._id }]);
        } catch (error) {
            console.error("❌ Lỗi khi thêm lịch trình:", error);
        }
    };
    const updateEvent = async (event) => {
        try {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("❌ Không tìm thấy token");

            const response = await fetch(`${API_URL}/schedules/${event.Id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    subject: event.Subject,
                    startTime: event.StartTime,
                    endTime: event.EndTime,
                    categoryColor: event.CategoryColor
                })
            });


            if (!response.ok) throw new Error("Lỗi cập nhật lịch trình");

            const result = await response.json();

            // Cập nhật dữ liệu trong state
            setEvents(events.map(e => (e.Id === event.Id ? event : e)));
        } catch (error) {
            console.error("❌ Lỗi khi cập nhật lịch trình:", error);
        }
    };

    const deleteEvent = async (eventId) => {
        try {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("❌ Không tìm thấy token");

            const response = await fetch(`${API_URL}/schedules/${eventId}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                }
            });

            if (!response.ok) throw new Error("Lỗi xóa lịch trình");

            const result = await response.json();

            setEvents(events.filter(e => e.Id !== eventId));
        } catch (error) {
            console.error("❌ Lỗi khi xóa lịch trình:", error);
        }
    };
    return (
        <main className='pt-14'>
            <ScheduleComponent
                width="100%"
                height="650px"
                eventSettings={{
                    dataSource: events,
                    fields: {
                        id: "Id",
                        subject: { name: "Subject" },
                        startTime: { name: "StartTime" },
                        endTime: { name: "EndTime" }
                    }
                }}
                actionBegin={(args) => {
                    if (args.requestType === "eventCreate") {
                        addEvent(args.data[0]);
                    } else if (args.requestType === "eventChange") {
                        const updatedEvent = Array.isArray(args.data) ? args.data[0] : args.data;
                        updateEvent(updatedEvent);
                    } else if (args.requestType === "eventRemove") {
                        const eventToDelete = Array.isArray(args.data) ? args.data[0] : args.data;
                        deleteEvent(eventToDelete.Id);
                    }
                }}>
                <ViewsDirective>
                    <ViewDirective option="Day" />
                    <ViewDirective option="Week" />
                    <ViewDirective option="Month" />
                    <ViewDirective option="Agenda" />
                </ViewsDirective>
                <Inject services={[Day, Week, Month, Agenda]} />
            </ScheduleComponent>
        </main>
    );
};

export default Schedule;
