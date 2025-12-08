import React, { useState } from 'react';
import useRooms from '../hooks/useRooms';
import RoomSearch from '../components/rooms/RoomSearch';
import RoomCard from '../components/rooms/RoomCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import Pagination from '../components/common/Pagination';
import { DoorOpen } from 'lucide-react';

const Rooms = () => {
  const [filters, setFilters] = useState({
    searchTerm: '',
    status: '',
    page: 0,
    size: 9,
  });

  const { rooms, page, isLoading, isFetching, error } = useRooms(filters);

  const handlePageChange = (newPage) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      page: newPage,
    }));
  };

  const handleFilterChange = (newFilters) => {
    setFilters({
      ...filters,
      ...newFilters,
      page: 0, // Reset to first page on filter change
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold tracking-tight">Rooms</h1>
      </div>
      
      <RoomSearch filters={filters} setFilters={handleFilterChange} />

      {isLoading && (
        <div className="flex justify-center p-8">
          <LoadingSpinner size="lg" />
        </div>
      )}

      {!isLoading && error && (
        <EmptyState
          icon={<DoorOpen className="w-12 h-12" />}
          title="An Error Occurred"
          message={`Failed to fetch rooms: ${error.message}`}
        />
      )}

      {!isLoading && !error && rooms.length === 0 && (
        <EmptyState
          icon={<DoorOpen className="w-12 h-12" />}
          title="No Rooms Found"
          message="Try adjusting your search filters."
        />
      )}

      {!isLoading && !error && rooms.length > 0 && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {rooms.map((room) => (
              <RoomCard key={room.id} room={room} />
            ))}
          </div>
          <Pagination
            currentPage={page?.number}
            totalPages={page?.totalPages}
            onPageChange={handlePageChange}
          />
        </>
      )}
    </div>
  );
};

export default Rooms;
