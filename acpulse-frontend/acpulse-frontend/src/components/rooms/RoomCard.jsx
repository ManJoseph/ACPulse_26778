import React from 'react';
import { useNavigate } from 'react-router-dom';
import { MapPin, Clock, User, Users } from 'lucide-react';
import Card from '../common/Card';
import Badge from '../common/Badge';
import { ROOM_STATUS } from '../../utils/constants';

const RoomCard = ({ room }) => {
  const navigate = useNavigate();

  const getStatusBadge = (status) => {
    switch (status) {
      case ROOM_STATUS.AVAILABLE:
        return <Badge color="green">Available</Badge>;
      case ROOM_STATUS.OCCUPIED:
        return <Badge color="red">Occupied</Badge>;
      case ROOM_STATUS.MAINTENANCE:
        return <Badge color="yellow">Maintenance</Badge>;
      default:
        return <Badge color="gray">Unknown</Badge>;
    }
  };

  const handleCardClick = () => {
    navigate(`/rooms/${room.id}`);
  };

  return (
    <Card
      onClick={handleCardClick}
      className="hover:shadow-lg transition-shadow cursor-pointer"
    >
      <Card.Body>
        <div className="flex justify-between items-start">
          <h3 className="text-lg font-bold">{room.name}</h3>
          {getStatusBadge(room.status)}
        </div>
        <div className="text-sm text-gray-500 dark:text-gray-400 mt-2">
          <div className="flex items-center gap-2">
            <MapPin className="w-4 h-4" />
            <span>{room.location.name}</span>
          </div>
        </div>
        <div className="mt-4 border-t border-gray-200 dark:border-dark-700 pt-4">
          {room.status === ROOM_STATUS.OCCUPIED && room.occupiedBy ? (
            <div className="flex items-center gap-2 text-sm">
              <User className="w-4 h-4 text-red-500" />
              <span>Occupied by <strong>{room.occupiedBy.name}</strong></span>
            </div>
          ) : (
            <div className="flex items-center gap-2 text-sm text-green-600">
              <Users className="w-4 h-4" />
              <span>Capacity: {room.capacity}</span>
            </div>
          )}
          {room.occupiedUntil && (
            <div className="flex items-center gap-2 text-sm mt-2">
              <Clock className="w-4 h-4" />
              <span>Until {new Date(room.occupiedUntil).toLocaleTimeString()}</span>
            </div>
          )}
        </div>
      </Card.Body>
    </Card>
  );
};

export default RoomCard;
