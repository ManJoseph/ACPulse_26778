import React from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { toast } from 'react-hot-toast';
import { Mail, Phone, MapPin, Building, Briefcase, Calendar } from 'lucide-react';

import { lecturerService } from '../../services';
import { Card, Avatar, Badge, LoadingSpinner, EmptyState } from '../common';
import { LECTURER_STATUS } from '../../utils/constants';

const LecturerProfile = () => {
    const { lecturerId } = useParams();

    const { data: lecturer, isLoading, error } = useQuery({
        queryKey: ['lecturer', lecturerId],
        queryFn: () => lecturerService.getLecturerById(lecturerId),
        onError: (err) => {
            toast.error(err.message || 'Failed to fetch lecturer details.');
        },
    });

    if (isLoading) return <LoadingSpinner fullScreen text="Loading Lecturer Profile..." />;
    if (error) return <EmptyState title="Error" description={error.message} />;
    if (!lecturer) return <EmptyState title="Lecturer Not Found" />;

    return (
        <div className="max-w-4xl mx-auto space-y-6">
            {/* Header */}
            <Card>
                <Card.Body>
                    <div className="flex flex-col md:flex-row items-center gap-6">
                        <Avatar src={lecturer.profilePicture} name={lecturer.name} size="2xl" />
                        <div className="flex-1 text-center md:text-left">
                            <div className="flex items-center justify-center md:justify-start gap-2">
                                <h1 className="text-3xl font-bold">{lecturer.name}</h1>
                                <Badge status={lecturer.status?.status}>{lecturer.status?.status}</Badge>
                            </div>
                            <p className="text-gray-500 dark:text-gray-400 text-lg mt-1">{lecturer.department}</p>
                            {lecturer.status?.message && (
                                <p className="text-sm text-gray-600 dark:text-gray-300 mt-2 p-2 bg-gray-50 dark:bg-dark-800 rounded-md">
                                    Status Update: {lecturer.status.message}
                                </p>
                            )}
                        </div>
                    </div>
                </Card.Body>
            </Card>

            {/* Contact Information */}
            <Card>
                <Card.Header>
                    <Card.Title>Contact Information</Card.Title>
                </Card.Header>
                <Card.Body>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <InfoItem icon={<Mail />} label="Email" value={lecturer.email} />
                        {lecturer.phoneNumber && <InfoItem icon={<Phone />} label="Phone" value={lecturer.phoneNumber} />}
                        {lecturer.office && <InfoItem icon={<Building />} label="Office" value={lecturer.office.name} />}
                        {lecturer.location && <InfoItem icon={<MapPin />} label="Location" value={lecturer.location.name} />}
                    </div>
                </Card.Body>
            </Card>

            {/* Schedule (Placeholder) */}
            <Card>
                <Card.Header>
                    <Card.Title>Schedule</Card.Title>
                </Card.Header>
                <Card.Body>
                    <EmptyState
                        icon={<Calendar />}
                        title="Schedule Coming Soon"
                        description="Detailed schedule information will be available here."
                    />
                </Card.Body>
            </Card>
        </div>
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

export default LecturerProfile;
