import React from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-hot-toast';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Lock } from 'lucide-react';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import { validationRules } from '../utils/validators';

const ResetPassword = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');

    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();

    const onSubmit = async (data) => {
        if (data.newPassword !== data.confirmPassword) {
            toast.error('New passwords do not match.');
            return;
        }
        try {
            // Here you would call your auth service to reset the password
            await authService.resetPassword(token, data.newPassword);
            toast.success('Password has been reset successfully!');
            navigate('/login');
        } catch (error) {
            toast.error(error.message || 'Failed to reset password. The link may be invalid or expired.');
        }
    };

    if (!token) {
        return (
            <div className="flex flex-col items-center justify-center h-full text-center">
                <h1 className="text-2xl font-bold">Invalid Reset Link</h1>
                <p className="mt-2">The password reset link is missing or invalid.</p>
            </div>
        )
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-dark-950 p-4">
            <div className="w-full max-w-md">
                 <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold gradient-text">Reset Your Password</h1>
                    <p className="text-gray-500 dark:text-gray-400 mt-2">
                        Choose a new password for your account.
                    </p>
                </div>
                <Card>
                    <Card.Body>
                        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                            <Input
                                label="New Password"
                                id="newPassword"
                                type="password"
                                leftIcon={<Lock />}
                                error={errors.newPassword?.message}
                                {...register('newPassword', validationRules.password)}
                            />
                            <Input
                                label="Confirm New Password"
                                id="confirmPassword"
                                type="password"
                                leftIcon={<Lock />}
                                error={errors.confirmPassword?.message}
                                {...register('confirmPassword', validationRules.password)}
                            />
                            <Button type="submit" variant="primary" className="w-full" loading={isSubmitting}>
                                Reset Password
                            </Button>
                        </form>
                    </Card.Body>
                </Card>
            </div>
        </div>
    );
};

export default ResetPassword;
