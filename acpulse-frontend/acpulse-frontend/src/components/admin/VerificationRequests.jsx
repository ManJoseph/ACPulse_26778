import React, { useState, useMemo } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'react-hot-toast';
import { Check, X, Clock, User } from 'lucide-react';
import { format } from 'date-fns';

import { adminService } from '../../services';
import { Table, Button, LoadingSpinner, EmptyState, Modal, Badge, Avatar } from '../common';
import { VERIFICATION_STATUS } from '../../utils/constants';

const VerificationRequests = () => {
    const queryClient = useQueryClient();
    const [statusFilter, setStatusFilter] = useState(VERIFICATION_STATUS.PENDING);
    const [action, setAction] = useState(null); // { type: 'approve' | 'reject', request: {...} }

    const { data: requests, isLoading, error } = useQuery({
        queryKey: ['verificationRequests', statusFilter],
        queryFn: () => adminService.getVerificationRequests(statusFilter)
    });

    const approveMutation = useMutation({
        mutationFn: adminService.approveUser,
        onSuccess: () => {
            toast.success('User approved successfully!');
            queryClient.invalidateQueries('verificationRequests');
            setAction(null);
        },
        onError: (err) => toast.error(err.message || 'Failed to approve user.'),
    });

    const rejectMutation = useMutation({
        mutationFn: adminService.rejectUser,
        onSuccess: () => {
            toast.success('User rejected successfully!');
            queryClient.invalidateQueries('verificationRequests');
            setAction(null);
        },
        onError: (err) => toast.error(err.message || 'Failed to reject user.'),
    });

    const columns = useMemo(() => [
        {
            key: 'user',
            label: 'User',
            render: (_, row) => (
                <div className="flex items-center gap-3">
                    <Avatar name={row.userName} size="sm" />
                    <div>
                        <p className="font-medium">{row.userName}</p>
                        <p className="text-xs text-gray-500">{row.email}</p>
                    </div>
                </div>
            )
        },
        { key: 'requestType', label: 'Role' },
        { key: 'submittedId', label: 'ID Number' },
        {
            key: 'submittedAt',
            label: 'Request Date',
            render: (date) => format(new Date(date), 'MMM d, yyyy')
        },
        {
            key: 'actions',
            label: 'Actions',
            render: (_, row) => (
                statusFilter === VERIFICATION_STATUS.PENDING && (
                    <div className="flex gap-2">
                        <Button size="sm" variant="success" onClick={() => setAction({ type: 'approve', request: row })}>
                            <Check className="w-4 h-4" /> Approve
                        </Button>
                        <Button size="sm" variant="danger" onClick={() => setAction({ type: 'reject', request: row })}>
                            <X className="w-4 h-4" /> Reject
                        </Button>
                    </div>
                )
            )
        }
    ], [statusFilter]);
    
    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold tracking-tight">Verification Requests</h1>
                <div className="flex gap-2 p-1 bg-gray-100 dark:bg-dark-800 rounded-lg">
                    {Object.values(VERIFICATION_STATUS).map(status => (
                        <Button
                            key={status}
                            variant={statusFilter === status ? 'primary' : 'ghost'}
                            size="sm"
                            onClick={() => setStatusFilter(status)}
                        >
                            {status}
                        </Button>
                    ))}
                </div>
            </div>

            {isLoading && <LoadingSpinner />}
            {error && <EmptyState title="Error" description={error.message} />}
            {!isLoading && !error && (
                <Table
                    columns={columns}
                    data={requests || []}
                    emptyMessage="No verification requests found for this status."
                />
            )}

            {/* Action Modals */}
            {action?.type === 'approve' && (
                <Modal isOpen={true} onClose={() => setAction(null)} title="Approve User">
                    <p>Are you sure you want to approve the user <span className="font-semibold">{action.request.userName}</span> as a <span className="font-semibold">{action.request.requestType}</span>?</p>
                    <Modal.Footer
                        onCancel={() => setAction(null)}
                        onConfirm={() => approveMutation.mutate(action.request.requestId)}
                        confirmText="Approve"
                        loading={approveMutation.isLoading}
                    />
                </Modal>
            )}

            {action?.type === 'reject' && (
                <RejectModal 
                    isOpen={true} 
                    onClose={() => setAction(null)} 
                    onSubmit={(reason) => rejectMutation.mutate({ requestId: action.request.requestId, reason })}
                    isLoading={rejectMutation.isLoading}
                    user={{ name: action.request.userName }}
                />
            )}
        </div>
    );
};

const RejectModal = ({ isOpen, onClose, onSubmit, isLoading, user }) => {
    const [reason, setReason] = useState('');

    const handleSubmit = () => {
        if (!reason) {
            toast.error('Please provide a reason for rejection.');
            return;
        }
        onSubmit(reason);
    }
    
    return (
        <Modal isOpen={isOpen} onClose={onClose} title={`Reject ${user.name}`}>
            <div className="space-y-4">
                <p>Please provide a reason for rejecting this verification request. This will be sent to the user.</p>
                <textarea
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    className="w-full input-base"
                    rows="4"
                    placeholder="e.g., Identification number does not match records."
                />
            </div>
             <Modal.Footer
                onCancel={onClose}
                onConfirm={handleSubmit}
                confirmText="Reject"
                loading={isLoading}
            />
        </Modal>
    );
}


export default VerificationRequests;
