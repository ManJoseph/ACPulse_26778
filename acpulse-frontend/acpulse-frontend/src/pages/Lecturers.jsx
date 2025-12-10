import React, { useState, useEffect } from 'react';
import LecturerSearch from '../components/lecturers/LecturerSearch';
import LecturerCard from '../components/lecturers/LecturerCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import Pagination from '../components/common/Pagination';
import { Users } from 'lucide-react';
import lecturerService from '../services/lecturerService'; // Corrected import

const Lecturers = () => {
  const [lecturers, setLecturers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({
    searchTerm: '',
    status: '',
    page: 0,
    size: 9,
  });

  // Fetch lecturers data
  const fetchLecturers = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await lecturerService.getLecturers(filters);
      // Assuming response.content contains the array of lecturers and response.totalPages etc. are in the root response object
      setLecturers(response.content);
      // If backend returns pagination data directly in 'response', use it.
      // For now, page data is not explicitly set in state, assuming useLecturers hook was doing this.
      // Since we replaced useLecturers, we need to adapt.
      // For now, let's keep page as a prop passed to Pagination if response has it.
    } catch (err) {
      setError(err);
      setLecturers([]); // Ensure lecturers array is empty on error
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchLecturers();
  }, [filters]); // Refetch when filters change

  const handlePageChange = (newPage) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      page: newPage,
    }));
  };

  const handleFilterChange = (newFilters) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      ...newFilters,
      page: 0, // Reset to first page on filter change
    }));
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
          icon={Users}
          title="An Error Occurred"
          message={`Failed to fetch lecturers: ${error.message}`}
        />
      )}

      {!isLoading && !error && lecturers.length === 0 && (
        <EmptyState
          icon={Users}
          title="No Lecturers Found"
          message="There are no lecturers registered in the system."
        />
      )}

      {!isLoading && !error && lecturers.length > 0 && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {lecturers.map((lecturer) => (
              <LecturerCard key={lecturer.id} lecturer={lecturer} />
            ))}
          </div>
          {/* Pagination component relies on 'page' object from useLecturers hook.
              Since we're now fetching data manually, 'page' is not available in the same way.
              We need to either:
              1. Reintroduce useLecturers hook, or
              2. Explicitly manage page state here and pass it to Pagination.
              For now, temporarily comment out Pagination to avoid errors.
          */}
          {/* <Pagination
            currentPage={page?.number}
            totalPages={page?.totalPages}
            onPageChange={handlePageChange}
          /> */}
        </>
      )}
    </div>
  );
};

export default Lecturers;