import React, { useState, useEffect } from 'react';
import LecturerSearch from '../components/lecturers/LecturerSearch';
import LecturerCard from '../components/lecturers/LecturerCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import Pagination from '../components/common/Pagination';
import { Users } from 'lucide-react';
import lecturerService from '../services/lecturerService'; // Corrected import

const Lecturers = () => {
  console.log("Lecturers component rendered");

  const [lecturers, setLecturers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({
    searchTerm: '',
    status: '',
    page: 0,
    size: 9,
  });

  console.log('Lecturers component filters state:', filters);

  // Fetch lecturers data
  const fetchLecturers = async () => {
    console.log('Fetching lecturers with filters:', filters);
    setIsLoading(true);
    setError(null);
    try {
      const response = await lecturerService.getLecturers(filters);
      console.log('Lecturer API response:', response);
      setLecturers(response || []); // Directly use the response array, or an empty array if null/undefined
      // Pagination logic needs to be managed if 'page' state is explicitly used.
      // For now, response.content is directly used.
    } catch (err) {
      console.error('Error fetching lecturers:', err);
      setError(err);
      setLecturers([]); // Ensure lecturers array is empty on error
    } finally {
      setIsLoading(false);
      console.log('Finished fetching lecturers. isLoading:', false);
    }
  };

  useEffect(() => {
    fetchLecturers();
  }, [filters]); // Refetch when filters change

  // Log component state changes
  useEffect(() => {
    console.log('Lecturers component state updated:', { lecturers, isLoading, error });
  }, [lecturers, isLoading, error]);

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