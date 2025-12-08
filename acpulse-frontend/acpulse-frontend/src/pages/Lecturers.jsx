import React, { useState } from 'react';
import useLecturers from '../hooks/useLecturers';
import LecturerSearch from '../components/lecturers/LecturerSearch';
import LecturerCard from '../components/lecturers/LecturerCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import Pagination from '../components/common/Pagination';
import { Users } from 'lucide-react';

const Lecturers = () => {
  const [filters, setFilters] = useState({
    searchTerm: '',
    status: '',
    page: 0,
    size: 9,
  });

  const { lecturers, page, isLoading, isFetching, error } = useLecturers(filters);

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
        <h1 className="text-3xl font-bold tracking-tight">Lecturers</h1>
      </div>
      
      <LecturerSearch filters={filters} setFilters={handleFilterChange} />

      {isLoading && (
        <div className="flex justify-center p-8">
          <LoadingSpinner size="lg" />
        </div>
      )}

      {!isLoading && error && (
        <EmptyState
          icon={<Users className="w-12 h-12" />}
          title="An Error Occurred"
          message={`Failed to fetch lecturers: ${error.message}`}
        />
      )}

      {!isLoading && !error && lecturers.length === 0 && (
        <EmptyState
          icon={<Users className="w-12 h-12" />}
          title="No Lecturers Found"
          message="Try adjusting your search filters."
        />
      )}

      {!isLoading && !error && lecturers.length > 0 && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {lecturers.map((lecturer) => (
              <LecturerCard key={lecturer.id} lecturer={lecturer} />
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

export default Lecturers;
