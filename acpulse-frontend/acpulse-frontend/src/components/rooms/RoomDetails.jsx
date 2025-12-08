import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'react-hot-toast';
import { format } from 'date-fns';
import { MapPin, Users, Clock, User, ShieldCheck, AlertTriangle } from 'lucide-react';

import { roomService } from '../../services';
import { useAuthStore } from '../../store';
import { ROLES, ROOM_STATUS } from '../../utils/constants';

import { Card, Button, Badge, LoadingSpinner, EmptyState, Modal, Input, Select } from '../common';

const RoomDetails = () => {
    const { roomId } = useParams();
    const queryClient = useQueryClient();
    const { user } = useAuthStore();
    
    const [occupyModalOpen, setOccupyModalOpen] = useState(false);
    const [extendModalOpen, setExtendModalOpen] = useState(false);
    const [releaseModalOpen, setReleaseModalOpen] = useState(false);

    const { data: room, isLoading, error } = useQuery(
        ['room', roomId],
        () => roomService.getRoomById(roomId)
    );

    // --- Mutations ---
    const occupyMutation = useMutation(roomService.occupyRoom, {
        onSuccess: () => {
            toast.success('Room occupied successfully!');
            queryClient.invalidateQueries(['room', roomId]);
            queryClient.invalidateQueries(['rooms']);
            setOccupyModalOpen(false);
        },
        onError: (err) => toast.error(err.message || 'Failed to occupy room.'),
    });

    const extendMutation = useMutation(roomService.extendRoom, {
        onSuccess: () => {
            toast.success('Occupation extended successfully!');
            queryClient.invalidateQueries(['room', roomId]);
            queryClient.invalidateQueries(['rooms']);
            setExtendModalOpen(false);
        },
        onError: (err) => toast.error(err.message || 'Failed to extend occupation.'),
    });
    
    const releaseMutation = useMutation(roomService.releaseRoom, {
        onSuccess: () => {
            toast.success('Room released successfully!');
            queryClient.invalidateQueries(['room', roomId]);
            queryClient.invalidateQueries(['rooms']);
            setReleaseModalOpen(false);
        },
        onError: (err) => toast.error(err.message || 'Failed to release room.'),
    });

    // --- Render Logic ---
    if (isLoading) return <LoadingSpinner fullScreen text="Loading Room Details..." />;
    if (error) return <EmptyState title="Error" description={error.message} icon={AlertTriangle} />;
    if (!room) return <EmptyState title="Room Not Found" />;

    const isLecturer = user?.role === ROLES.LECTURER;
    const canOccupy = isLecturer && room.status === ROOM_STATUS.AVAILABLE;
    const canManage = isLecturer && room.occupiedBy?.id === user?.id;

    return (
        <>
            <div className="space-y-6">
                {/* Header */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center">
                    <div>
                        <h1 className="text-4xl font-bold tracking-tight">{room.name}</h1>
                        <div className="flex items-center gap-2 text-gray-500 dark:text-gray-400 mt-2">
                            <MapPin className="w-5 h-5" />
                            <span>{room.location.name}</span>
                        </div>
                    </div>
                    <div className="mt-4 md:mt-0">
                        <Badge status={room.status} size="lg">{room.status}</Badge>
                    </div>
                </div>

                {/* Action Buttons */}
                {isLecturer && (
                    <div className="flex gap-2">
                        {canOccupy && <Button onClick={() => setOccupyModalOpen(true)}>Occupy Room</Button>}
                        {canManage && <Button onClick={() => setExtendModalOpen(true)} variant="outline">Extend Occupation</Button>}
                        {canManage && <Button onClick={() => setReleaseModalOpen(true)} variant="danger">Release Room</Button>}
                    </div>
                )}


                {/* Details Card */}
                <Card>
                    <Card.Body className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <InfoItem icon={<Users />} label="Capacity" value={room.capacity} />
                        <InfoItem icon={<ShieldCheck />} label="Type" value={room.type} />
                        {room.status === ROOM_STATUS.OCCUPIED && room.occupiedBy && (
                            <>
                                <InfoItem icon={<User />} label="Occupied By" value={room.occupiedBy.name} />
                                <InfoItem icon={<Clock />} label="Occupied Until" value={format(new Date(room.occupiedUntil), 'p, MMM d')} />
                                {room.occupiedFor && <InfoItem icon={<AlertTriangle />} label="Purpose" value={room.occupiedFor} />}
                            </>
                        )}
                    </Card.Body>
                </Card>
                
                {/* Add room schedule component here later if needed */}
            </div>

            {/* Modals */}
            {canOccupy && <OccupyModal isOpen={occupyModalOpen} onClose={() => setOccupyModalOpen(false)} onSubmit={occupyMutation.mutate} isLoading={occupyMutation.isLoading} roomId={room.id} />}
            {canManage && <ExtendModal isOpen={extendModalOpen} onClose={() => setExtendModalOpen(false)} onSubmit={(data) => extendMutation.mutate({roomId: room.id, data})} isLoading={extendMutation.isLoading} />}
            {canManage && <ReleaseModal isOpen={releaseModalOpen} onClose={() => setReleaseModalOpen(false)} onConfirm={() => releaseMutation.mutate(room.id)} isLoading={releaseMutation.isLoading} />}

        </>
    );
};

const InfoItem = ({ icon, label, value }) => (
    <div className="flex items-start gap-4">
        <div className="p-2 bg-gray-100 dark:bg-dark-800 rounded-lg text-gray-600 dark:text-gray-300">
            {icon}
        </div>
        <div>
            <p className="text-sm text-gray-500 dark:text-gray-400">{label}</p>
            <p className="font-semibold">{value}</p>
        </div>
    </div>
);


// --- Modals ---
const OccupyModal = ({ isOpen, onClose, onSubmit, isLoading, roomId }) => {
    const { register, handleSubmit } = useForm();
    
    const handleFormSubmit = (data) => {
        onSubmit({ ...data, roomId });
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Occupy Room">
            <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
                <Input label="Purpose" id="occupiedFor" {...register('occupiedFor', { required: 'Purpose is required.'})} placeholder="e.g., Student Consultation" />
                <Select label="Duration (in minutes)" id="duration" {...register('duration', { valueAsNumber: true, required: true })}>
                    <option value={30}>30 minutes</option>
                    <option value={60}>1 hour</option>
                    <option value={90}>1.5 hours</option>
                    <option value={120}>2 hours</option>
                </Select>
                <Modal.Footer onCancel={onClose} onConfirm={handleSubmit(handleFormSubmit)} confirmText="Occupy" loading={isLoading} />
            </form>
        </Modal>
    );
};

const ExtendModal = ({ isOpen, onClose, onSubmit, isLoading }) => {
    const { register, handleSubmit } = useForm();

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Extend Occupation">
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <Select label="Extend by (in minutes)" id="duration" {...register('duration', { valueAsNumber: true, required: true })}>
                    <option value={15}>15 minutes</option>
                    <option value={30}>30 minutes</option>
                    <option value={45}>45 minutes</option>
                    <option value={60}>1 hour</option>
                </Select>
                <Modal.Footer onCancel={onClose} onConfirm={handleSubmit(onSubmit)} confirmText="Extend" loading={isLoading} />
            </form>
        </Modal>
    );
};

const ReleaseModal = ({ isOpen, onClose, onConfirm, isLoading }) => (
    <Modal isOpen={isOpen} onClose={onClose} title="Release Room" description="Are you sure you want to release this room? This action cannot be undone.">
        <Modal.Footer
            onCancel={onClose}
            onConfirm={onConfirm}
            confirmText="Yes, Release It"
            loading={isLoading}
        />
    </Modal>
);

export default RoomDetails;