import React from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-hot-toast';
import { Link } from 'react-router-dom';
import { Mail } from 'lucide-react';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import { validationRules } from '../utils/validators';

const ForgotPassword = () => {
    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();

    const onSubmit = async (data) => {
        try {
            // Here you would call your auth service to send a password reset link
            await authService.forgotPassword(data.email);
            toast.success('If an account with that email exists, a password reset link has been sent.');
        } catch (error) {
            toast.error(error.message || 'Failed to send reset link.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-dark-950 p-4">
            <div className="w-full max-w-md">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold gradient-text">Forgot Your Password?</h1>
                    <p className="text-gray-500 dark:text-gray-400 mt-2">
                        No worries, we'll send you reset instructions.
                    </p>
                </div>
                <Card>
                    <Card.Body>
                        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                            <Input
                                label="Email"
                                id="email"
                                type="email"
                                placeholder="your.email@auca.ac.rw"
                                leftIcon={<Mail className="w-5 h-5" />}
                                error={errors.email?.message}
                                {...register('email', validationRules.email)}
                            />
                            <Button type="submit" variant="primary" className="w-full" loading={isSubmitting}>
                                Send Reset Link
                            </Button>
                        </form>
                    </Card.Body>
                </Card>
                <div className="mt-6 text-center text-sm">
                    <p className="text-gray-600 dark:text-gray-400">
                        <Link to="/login" className="font-medium text-primary-600 hover:text-primary-500">
                            &larr; Back to Sign in
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default ForgotPassword;
