import React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'react-hot-toast';
import { format } from 'date-fns';
import { CheckCircle } from 'lucide-react';

import { adminService } from '../../services';
import { useAuthStore } from '../../store';
import { Table, Button, LoadingSpinner, EmptyState, Avatar } from '../common';

const PasswordResetRequests = () => {
    const queryClient = useQueryClient();
    const { user } = useAuthStore();
    const adminId = user?.userId;

    const { data: requests, isLoading, error } = useQuery({
        queryKey: ['passwordResetRequests'],
        queryFn: () => adminService.getPasswordResetRequests(),
    });

    const approveMutation = useMutation({
        mutationFn: adminService.approvePasswordResetRequest,
        onSuccess: () => {
            toast.success('Password reset request approved. User has been notified.');
            queryClient.invalidateQueries({ queryKey: ['passwordResetRequests'] });
        },
        onError: (err) => {
            toast.error(err.message || 'Failed to approve request.');
        },
    });

    const handleApprove = (requestId) => {
        if (!adminId) {
            toast.error('Could not identify admin. Please log in again.');
            return;
        }
        approveMutation.mutate({ requestId, adminId });
    };

    const columns = [
        {
            key: 'user',
            label: 'User',
            render: (_, row) => (
                <div className="flex items-center gap-3">
                    <Avatar name={row.user.name} size="sm" />
                    <div>
                        <p className="font-medium">{row.user.name}</p>
                        <p className="text-xs text-gray-500">{row.user.email}</p>
                    </div>
                </div>
            ),
        },
        {
            key: 'createdAt',
            label: 'Request Date',
            render: (date) => format(new Date(date), 'MMM d, yyyy, h:mm a'),
        },
        {
            key: 'actions',
            label: 'Actions',
            render: (_, row) => (
                <Button 
                    size="sm" 
                    variant="success" 
                    onClick={() => handleApprove(row.id)}
                    loading={approveMutation.isLoading && approveMutation.variables?.requestId === row.id}
                >
                    <CheckCircle className="w-4 h-4 mr-2" />
                    Approve & Send Link
                </Button>
            ),
        },
    ];

    return (
        <div className="space-y-6">
            <h1 className="text-3xl font-bold tracking-tight">Password Reset Requests</h1>
            
            {isLoading && <LoadingSpinner />}
            {error && <EmptyState title="Error" description={error.message} />}
            {!isLoading && !error && (
                <Table
                    columns={columns}
                    data={requests || []}
                    emptyMessage="No pending password reset requests."
                />
            )}
        </div>
    );
};

export default PasswordResetRequests;