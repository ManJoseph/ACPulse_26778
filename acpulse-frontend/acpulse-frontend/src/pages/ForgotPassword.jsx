import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-hot-toast';
import { Link } from 'react-router-dom';
import { Mail, CheckCircle } from 'lucide-react';
import authService from '../services/authService';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import { validationRules } from '../utils/validators';

const ForgotPassword = () => {
    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
    const [isSubmitted, setIsSubmitted] = useState(false);

    const onSubmit = async (data) => {
        try {
            await authService.forgotPassword(data);
            toast.success('Your password reset request has been submitted for approval.');
            setIsSubmitted(true);
        } catch (error) {
            toast.error(error.message || 'Failed to submit request.');
        }
    };

    if (isSubmitted) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-dark-950 p-4">
                <div className="w-full max-w-md text-center">
                    <Card>
                        <Card.Body className="flex flex-col items-center justify-center p-8">
                            <CheckCircle className="w-16 h-16 text-green-500 mb-4" />
                            <h1 className="text-2xl font-bold mb-2">Request Submitted</h1>
                            <p className="text-gray-600 dark:text-gray-400 mb-6">
                                Your request to reset your password has been sent to the admin for approval. You will receive an email once it is approved.
                            </p>
                            <Link to="/login">
                                <Button variant="primary">&larr; Back to Login</Button>
                            </Link>
                        </Card.Body>
                    </Card>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-dark-950 p-4">
            <div className="w-full max-w-md">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold gradient-text">Forgot Your Password?</h1>
                    <p className="text-gray-500 dark:text-gray-400 mt-2">
                        Enter your email to request a password reset. An admin will review your request.
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
                                Request Reset
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
