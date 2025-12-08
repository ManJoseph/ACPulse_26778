import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { AnimatePresence, motion } from 'framer-motion';
import { User, Lock, Mail, Building, Briefcase, Phone, MapPin, Hash } from 'lucide-react';

import authService from '../../services/authService';
import locationService from '../../services/locationService';
import { validationRules } from '../../utils/validators';
import { ROLES } from '../../utils/constants';

import Button from '../common/Button';
import Input from '../common/Input';
import Select from '../common/Select';

const steps = [
  { id: 1, name: 'Account Details' },
  { id: 2, name: 'Personal Information' },
  { id: 3, name: 'Finalize' },
];

const SignupForm = () => {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(1);
  const [locations, setLocations] = useState({ provinces: [], districts: [], sectors: [] });
  const [isLoadingLocations, setIsLoadingLocations] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    watch,
    trigger,
    setError,
  } = useForm({
    mode: 'onChange',
  });

  const selectedProvince = watch('provinceId');
  const selectedDistrict = watch('districtId');

  // --- Location Data Fetching ---
  useEffect(() => {
    const fetchProvinces = async () => {
      try {
        setIsLoadingLocations(true);
        const provinces = await locationService.getLocationsByType('PROVINCE');
        setLocations(prev => ({ ...prev, provinces }));
      } catch (error) {
        toast.error('Failed to load location data.');
      } finally {
        setIsLoadingLocations(false);
      }
    };
    fetchProvinces();
  }, []);

  useEffect(() => {
    if (!selectedProvince) return;
    const fetchDistricts = async () => {
      try {
        setIsLoadingLocations(true);
        const districts = await locationService.getChildLocations(selectedProvince);
        setLocations(prev => ({ ...prev, districts, sectors: [] }));
      } catch (error) {
        toast.error('Failed to load districts.');
      } finally {
        setIsLoadingLocations(false);
      }
    };
    fetchDistricts();
  }, [selectedProvince]);

  useEffect(() => {
    if (!selectedDistrict) return;
    const fetchSectors = async () => {
      try {
        setIsLoadingLocations(true);
        const sectors = await locationService.getChildLocations(selectedDistrict);
        setLocations(prev => ({ ...prev, sectors }));
      } catch (error) {
        toast.error('Failed to load sectors.');
      } finally {
        setIsLoadingLocations(false);
      }
    };
    fetchSectors();
  }, [selectedDistrict]);

  // --- Form Navigation ---
  const handleNext = async () => {
    const fieldsToValidate = {
      1: ['name', 'email', 'password'],
      2: ['roleType', 'identificationNumber', 'department'],
    };
    const isValid = await trigger(fieldsToValidate[currentStep]);
    if (isValid) {
      setCurrentStep(prev => prev + 1);
    }
  };

  const handlePrev = () => {
    setCurrentStep(prev => prev - 1);
  };

  // --- Form Submission ---
  const onSubmit = async (data) => {
    const finalData = {
      ...data,
      locationId: data.sectorId || data.districtId || data.provinceId,
    };

    // Remove temporary location fields
    delete finalData.provinceId;
    delete finalData.districtId;
    delete finalData.sectorId;

    try {
      const response = await authService.register(finalData);
      toast.success(response.message || 'Registration successful! Please wait for admin approval.');
      navigate('/login');
    } catch (error) {
      const errorMessage = error.message || 'Registration failed. Please try again.';
      toast.error(errorMessage);
      setError('root.serverError', { type: 'manual', message: errorMessage });
    }
  };
  


  return (
    <>
      {/* Progress Bar */}
      <nav aria-label="Progress">
        <ol role="list" className="flex items-center mb-8">
          {steps.map((step, stepIdx) => (
            <li key={step.name} className={`flex-1 ${stepIdx !== steps.length - 1 ? 'pr-8 sm:pr-20' : ''} relative`}>
              {currentStep > step.id ? (
                <>
                  <div className="absolute inset-0 flex items-center" aria-hidden="true">
                    <div className="h-0.5 w-full bg-primary-600" />
                  </div>
                  <span
                    className="relative w-8 h-8 flex items-center justify-center bg-primary-600 rounded-full text-white cursor-pointer"
                    onClick={() => setCurrentStep(step.id)}
                  >
                    &#10003;
                  </span>
                </>
              ) : currentStep === step.id ? (
                <>
                  <div className="absolute inset-0 flex items-center" aria-hidden="true">
                    <div className="h-0.5 w-full bg-gray-200 dark:bg-dark-700" />
                  </div>
                  <span className="relative w-8 h-8 flex items-center justify-center bg-white dark:bg-dark-800 border-2 border-primary-600 rounded-full">
                    <span className="h-2.5 w-2.5 bg-primary-600 rounded-full" />
                  </span>
                </>
              ) : (
                <>
                  <div className="absolute inset-0 flex items-center" aria-hidden="true">
                    <div className="h-0.5 w-full bg-gray-200 dark:bg-dark-700" />
                  </div>
                  <span className="relative w-8 h-8 flex items-center justify-center bg-gray-100 dark:bg-dark-700 rounded-full text-gray-500">
                    {step.id}
                  </span>
                </>
              )}
            </li>
          ))}
        </ol>
      </nav>

      {/* Form Content */}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <AnimatePresence mode="wait">
          {currentStep === 1 && (
            <motion.div key={1} initial={{ x: 300, opacity: 0 }} animate={{ x: 0, opacity: 1 }} exit={{ x: -300, opacity: 0 }} className="space-y-6">
              <h2 className="text-lg font-medium">Step 1: Account Details</h2>
              <Input label="Full Name" id="name" leftIcon={<User />} error={errors.name?.message} {...register('name', validationRules.name)} />
              <Input label="Email" id="email" type="email" leftIcon={<Mail />} error={errors.email?.message} {...register('email', validationRules.email)} />
              <Input label="Password" id="password" type="password" leftIcon={<Lock />} error={errors.password?.message} {...register('password', validationRules.password)} />
            </motion.div>
          )}

          {currentStep === 2 && (
            <motion.div key={2} initial={{ x: 300, opacity: 0 }} animate={{ x: 0, opacity: 1 }} exit={{ x: -300, opacity: 0 }} className="space-y-6">
              <h2 className="text-lg font-medium">Step 2: Personal Information</h2>
               <Select label="Role" id="roleType" error={errors.roleType?.message} {...register('roleType', { required: 'Role is required' })}>
                <option value="">Select a role</option>
                <option value={ROLES.STUDENT}>Student</option>
                <option value={ROLES.LECTURER}>Lecturer</option>
                <option value={ROLES.STAFF}>Staff</option>
              </Select>
              <Input label="Identification Number" id="identificationNumber" leftIcon={<Hash />} error={errors.identificationNumber?.message} {...register('identificationNumber', validationRules.identificationNumber)} />
              <Input label="Department" id="department" leftIcon={<Briefcase />} error={errors.department?.message} {...register('department')} />
            </motion.div>
          )}

          {currentStep === 3 && (
             <motion.div key={3} initial={{ x: 300, opacity: 0 }} animate={{ x: 0, opacity: 1 }} exit={{ x: -300, opacity: 0 }} className="space-y-6">
              <h2 className="text-lg font-medium">Step 3: Contact & Location</h2>
              <Input label="Phone Number (Optional)" id="phoneNumber" type="tel" leftIcon={<Phone />} error={errors.phoneNumber?.message} {...register('phoneNumber')} />
              <Select label="Province" id="provinceId" error={errors.provinceId?.message} {...register('provinceId')} disabled={isLoadingLocations}>
                <option value="">Select Province</option>
                {locations.provinces.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
              </Select>
              <Select label="District" id="districtId" error={errors.districtId?.message} {...register('districtId')} disabled={!selectedProvince || isLoadingLocations}>
                <option value="">Select District</option>
                {locations.districts.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
              </Select>
               <Select label="Sector" id="sectorId" error={errors.sectorId?.message} {...register('sectorId')} disabled={!selectedDistrict || isLoadingLocations}>
                <option value="">Select Sector</option>
                {locations.sectors.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
              </Select>
              {errors.root?.serverError && <p className="text-sm text-red-500">{errors.root.serverError.message}</p>}
            </motion.div>
          )}
        </AnimatePresence>

        {/* Navigation Buttons */}
        <div className="flex justify-between pt-4">
          {currentStep > 1 ? (
            <Button type="button" variant="outline" onClick={handlePrev}>
              Previous
            </Button>
          ) : <div />}
          {currentStep < steps.length ? (
            <Button type="button" variant="primary" onClick={handleNext}>
              Next
            </Button>
          ) : (
            <Button type="submit" variant="success" loading={isSubmitting}>
              Complete Registration
            </Button>
          )}
        </div>
      </form>
    </>
  );
};

export default SignupForm;
