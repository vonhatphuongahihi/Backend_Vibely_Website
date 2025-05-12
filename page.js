"use client";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { loginUser, registerUser, checkUserAuth, getGoogleLoginUrl, getFacebookLoginUrl, getGithubLoginUrl } from "@/service/auth.service";
import { yupResolver } from "@hookform/resolvers/yup";
import { motion } from "framer-motion";
import { LogIn } from "lucide-react";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import toast from "react-hot-toast";
import * as yup from "yup";
import axios from 'axios';


const Page = () => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [rememberMe, setRememberMe] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [user, setUser] = useState(null);

  // Xử lý callback từ Google
  useEffect(() => {
    const token = searchParams.get('token');
    const error = searchParams.get('error');

    if (token) {
      // Lưu token
      localStorage.setItem('auth_token', token);

      // Lưu thông tin user
      const userId = searchParams.get('userId');
      const email = searchParams.get('email');
      const username = searchParams.get('username');

      // Cập nhật state user
      setUser({
        _id: userId,
        email,
        username
      });

      toast.success('Đăng nhập Google thành công');
      router.push('/');
    } else if (error) {
      console.error('Google login error:', error);
      toast.error('Đăng nhập Google thất bại: ' + error);
      router.push('/user-login');
    }
  }, [searchParams, router, setUser]);

  // Kiểm tra trạng thái đăng nhập khi component mount
  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const { isAuthenticated } = await checkUserAuth();
        if (isAuthenticated) {
          router.replace("/"); // Chuyển hướng về trang chủ nếu đã đăng nhập
        }
      } catch (error) {
        console.log('Chưa đăng nhập');
      } finally {
        setIsLoading(false);
      }
    };

    checkLoginStatus();
  }, [router]);

  const registerSchema = yup.object().shape({
    username: yup.string().required("Tên không được để trống"),
    email: yup
      .string()
      .email("Email không hợp lệ")
      .required("Email không được để trống"),
    password: yup
      .string()
      .min(6, "Mật khẩu phải có ít nhất 6 ký tự")
      .required("Mật khẩu không được để trống"),
    dateOfBirth: yup.date().required("Ngày sinh không được để trống"),
    gender: yup
      .string()
      .oneOf(["Nam", "Nữ", "Khác"], "Vui lòng chọn giới tính")
      .required("Giới tính không được để trống"),
  });
  const loginSchema = yup.object().shape({
    email: yup
      .string()
      .email("Email không hợp lệ")
      .required("Email không được để trống"),
    password: yup
      .string()
      .min(6, "Mật khẩu phải có ít nhất 6 ký tự")
      .required("Mật khẩu không được để trống"),
  });
  const {
    register: registerLogin,
    handleSubmit: handleSubmitLogin,
    reset: resetLoginForm,
    formState: { errors: errorsLogin },
  } = useForm({
    resolver: yupResolver(loginSchema),
  });

  const {
    register: registerSignUp,
    control,
    handleSubmit: handleSubmitSignUp,
    reset: resetSignUpForm,
    formState: { errors: errorsSignUp },
  } = useForm({
    resolver: yupResolver(registerSchema),
    defaultValues: { gender: "Nữ" },
  });

  const onSubmitRegister = async (data) => {
    try {
      // const result = await registerUser(data)
      // if (result.status === 'success') {
      await axios.post(`${process.env.NEXT_PUBLIC_BACKEND_URL}/auth/send-otp`, { email: data.email });
      // Lưu email tạm thời vào localStorage
      // localStorage.setItem('tempEmail', data.email);
      localStorage.setItem('tempRegisterData', JSON.stringify(data));
      // Chuyển hướng đến trang xác nhận OTP
      router.push('/user-login/code-confirm');
      // }
      toast.success('Vui lòng kiểm tra email để xác thực tài khoản')
    } catch (error) {
      console.error(error);
      toast.error('Email đã tồn tại')
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    resetLoginForm();
    resetSignUpForm()
  }, [resetLoginForm, resetSignUpForm])

  const onSubmitLogin = async (data) => {
    try {
      setIsSubmitting(true);
      const result = await loginUser(data);
      if (result.status === 'success') {
        toast.success('Đăng nhập tài khoản thành công');
        router.push('/');
      }
    } catch (error) {
      console.error(error);
      toast.error('Email hoặc mật khẩu không chính xác');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleGoogleLogin = () => {
    try {
      window.location.href = getGoogleLoginUrl();
    } catch (error) {
      console.error('Lỗi khi chuyển hướng đến Google:', error);
      toast.error('Không thể kết nối đến Google. Vui lòng thử lại sau.');
    }
  }

  const handleFacebookLogin = () => {
    try {
      window.location.href = getFacebookLoginUrl();
    } catch (error) {
      console.error('Lỗi khi chuyển hướng đến Facebook:', error);
      toast.error('Không thể kết nối đến Facebook. Vui lòng thử lại sau.');
    }
  }

  const handleGithubLogin = () => {
    try {
      window.location.href = getGithubLoginUrl();
    } catch (error) {
      console.error('Lỗi khi chuyển hướng đến GitHub:', error);
      toast.error('Không thể kết nối đến GitHub. Vui lòng thử lại sau.');
    }
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#F9FDFF] flex items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <div className="w-16 h-16 border-4 border-[#23CAF1] border-t-transparent rounded-full animate-spin"></div>
          <p className="text-[#086280] font-medium">Đang kiểm tra đăng nhập...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#F9FDFF] flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5 }}
      >
        <Card className="w-full max-w-md dark:text-white border-[#0E42D2]">
          <CardHeader>
            <CardTitle className="flex justify-center">
              <img src="/images/logo.png" alt="Vibely" className="w-20" />
            </CardTitle>
            <CardDescription className="text-center text-[#1CA2C1]">
              Cùng học tập và kết bạn ở khắp mọi nơi trên thế giới trên Vibely
            </CardDescription>
          </CardHeader>

          <CardContent>
            <Tabs defaultValue="login" className="w-full">
              <TabsList className="grid w-full grid-cols-2 bg-slate-200">
                <TabsTrigger
                  value="login"
                  className="text-[#086280] data-[state=active]:text-[#086280] data-[state=active]:font-bold"
                >
                  Đăng nhập
                </TabsTrigger>
                <TabsTrigger
                  value="signup"
                  className="text-[#086280] data-[state=active]:text-[#086280] data-[state=active]:font-bold"
                >
                  Đăng ký
                </TabsTrigger>
              </TabsList>
              {/* Tab đăng nhập */}
              <TabsContent value="login">
                <form onSubmit={handleSubmitLogin(onSubmitLogin)}>
                  <div className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="loginEmail" className="text-[#086280]">Email</Label>
                      <Input
                        id="loginEmail"
                        name="email"
                        type="email"
                        {...registerLogin("email")}
                        placeholder="Nhập email của bạn"
                        className="col-span-3 dark:border-gray-400 border-[#0E42D2] placeholder:text-gray-400"
                      />
                      {errorsLogin.email && (
                        <p className="text-red-500">
                          {errorsLogin.email.message}
                        </p>
                      )}
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="loginPassword" className="text-[#086280]">Mật khẩu</Label>
                      <Input
                        id="loginPassword"
                        name="password"
                        type="password"
                        {...registerLogin("password")}
                        placeholder="Nhập mật khẩu của bạn"
                        className="col-span-3 dark:border-gray-400 border-[#0E42D2] placeholder:text-gray-400"
                      />
                      {errorsLogin.password && (
                        <p className="text-red-500">
                          {errorsLogin.password.message}
                        </p>
                      )}
                    </div>
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-2">
                        {/* Toggle Switch */}
                        <div
                          className={`w-12 h-6 flex items-center rounded-full p-1 cursor-pointer transition ${rememberMe ? "bg-[#23CAF1]" : "bg-gray-300"
                            }`}
                          onClick={() => setRememberMe(!rememberMe)}
                        >
                          <div
                            className={`bg-white w-5 h-5 rounded-full shadow-md transform transition ${rememberMe ? "translate-x-6" : "translate-x-0"
                              }`}
                          ></div>
                        </div>
                        <span className="text-sm text-black cursor-pointer" onClick={() => setRememberMe(!rememberMe)}>
                          Ghi nhớ
                        </span>
                      </div>
                      <div className="text-sm text-[#086280] hover:text-[#1AA3C8] transition-colors duration-200 font-bold">
                        <a href="/forgot-password">Quên mật khẩu?</a>
                      </div>
                    </div>
                    <Button
                      className="w-full bg-[#23CAF1] text-white"
                      type="submit"
                      disabled={isSubmitting}
                    >
                      {isSubmitting ? (
                        <div className="flex items-center justify-center">
                          <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"></div>
                          Đang xử lý...
                        </div>
                      ) : (
                        <>
                          <LogIn className="mr-2 w-4 h-4" />Đăng nhập
                        </>
                      )}
                    </Button>
                  </div>
                </form>

                {/* Nút đăng nhập bằng Google chỉ hiển thị ở tab Đăng nhập */}
                <div className="relative w-full mt-4">
                  <div className="absolute inset-0 flex items-center">
                    <span className="w-[30%] border-t border-muted-foreground"></span>
                    <div className="w-[40%]"></div>
                    <span className="w-[30%] border-t border-muted-foreground"></span>
                  </div>
                  <div className="relative flex justify-center text-xs uppercase my-4">
                    <span className="px-2 text-muted-foreground">
                      Hoặc đăng nhập bằng
                    </span>
                  </div>
                </div>
                <div className="w-full gap-4">
                  <motion.div
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    className="space-y-4"
                  >
                    <Button variant="outline" className="w-full bg-slate-200" onClick={handleGoogleLogin}>
                      <svg className="mr-2 h-4 w-4" viewBox="0 0 24 24">
                        <path
                          d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                          fill="#4285F4"
                        />
                        <path
                          d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                          fill="#34A853"
                        />
                        <path
                          d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                          fill="#FBBC05"
                        />
                        <path
                          d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                          fill="#EA4335"
                        />
                        <path d="M1 1h22v22H1z" fill="none" />
                      </svg>
                      Google
                    </Button>

                    <Button variant="outline" className="w-full bg-slate-200" onClick={handleFacebookLogin}>
                      <svg className="mr-2 h-4 w-4" viewBox="0 0 24 24" fill="#1877F2">
                        <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
                      </svg>
                      Facebook
                    </Button>

                    <Button variant="outline" className="w-full bg-slate-200" onClick={handleGithubLogin}>
                      <svg className="mr-2 h-4 w-4" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z" />
                      </svg>
                      GitHub
                    </Button>
                  </motion.div>
                </div>
              </TabsContent>
              {/* Tab đăng ký */}
              <TabsContent value="signup">
                <form onSubmit={handleSubmitSignUp(onSubmitRegister)}>
                  <div className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="signupName" className="text-[#086280]">Tên người dùng</Label>
                      <Input
                        id="signupName"
                        name="username"
                        type="text"
                        {...registerSignUp("username")}
                        placeholder="Nhập tên người dùng của bạn"
                        className="col-span-3 dark:border-gray-400 border-[#0E42D2] placeholder:text-gray-400"
                      />
                      {errorsSignUp.username && (
                        <p className="text-red-500">
                          {errorsSignUp.username.message}
                        </p>
                      )}
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="loginEmail" className="text-[#086280]">Email</Label>
                      <Input
                        id="loginEmail"
                        name="email"
                        type="email"
                        {...registerSignUp("email")}
                        placeholder="Nhập email của bạn"
                        className="col-span-3 dark:border-gray-400 border-[#0E42D2] placeholder:text-gray-400"
                      />
                      {errorsSignUp.email && (
                        <p className="text-red-500">
                          {errorsSignUp.email.message}
                        </p>
                      )}
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="loginPassword" className="text-[#086280]">Mật khẩu</Label>
                      <Input
                        id="loginPassword"
                        name="password"
                        type="password"
                        {...registerSignUp("password")}
                        placeholder="Nhập mật khẩu của bạn"
                        className="col-span-3 dark:border-gray-400 border-[#0E42D2] placeholder:text-gray-400"
                      />
                      {errorsSignUp.password && (
                        <p className="text-red-500">
                          {errorsSignUp.password.message}
                        </p>
                      )}
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="confirmPassword" className="text-[#086280]">Mật khẩu</Label>
                      <Input
                        id="confirmPassword"
                        name="confirmpassword"
                        type="password"
                        placeholder="Nhập lại mật khẩu của bạn"
                        className="col-span-3 dark:border-gray-400 border-[#0E42D2] placeholder:text-gray-400"
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="signupBirthday" className="text-[#086280]">Ngày sinh</Label>
                      <Input
                        id="signupBirthday"
                        name="dateOfBirth"
                        type="date"
                        {...registerSignUp("dateOfBirth")}
                        className="col-span-3 dark:border-gray-400 border-[#0E42D2]"
                      />
                      {errorsSignUp.dateOfBirth && (
                        <p className="text-red-500">
                          {errorsSignUp.dateOfBirth.message}
                        </p>
                      )}
                    </div>
                    <div className="space-y-2">
                      <Label className="text-[#086280]">Giới tính</Label>
                      <Controller
                        name="gender"
                        control={control}
                        render={({ field }) => (
                          <RadioGroup
                            className="flex justify-between"
                            value={field.value}
                            onValueChange={field.onChange}  // Bắt sự kiện thay đổi giá trị
                          >
                            <div className="flex items-center space-x-2">
                              <RadioGroupItem value="Nam" id="male" />
                              <Label htmlFor="male">Nam</Label>
                            </div>
                            <div className="flex items-center space-x-2">
                              <RadioGroupItem value="Nữ" id="female" />
                              <Label htmlFor="female">Nữ</Label>
                            </div>
                            <div className="flex items-center space-x-2">
                              <RadioGroupItem value="Khác" id="other" />
                              <Label htmlFor="other">Khác</Label>
                            </div>
                          </RadioGroup>
                        )}
                      />
                      {errorsSignUp.gender && (
                        <p className="text-red-500">{errorsSignUp.gender.message}</p>
                      )}
                    </div>

                    <Button
                      className="w-full bg-[#23CAF1] text-white"
                      type="submit"
                      disabled={isSubmitting}
                    >
                      {isSubmitting ? (
                        <div className="flex items-center justify-center">
                          <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"></div>
                          Đang xử lý...
                        </div>
                      ) : (
                        <>
                          <LogIn className="mr-2 w-4 h-4" /> Đăng ký
                        </>
                      )}
                    </Button>
                  </div>
                </form>
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  );
};

export default Page;