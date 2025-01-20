import React from 'react';
import { useNavigate } from 'react-router-dom';
import * as trainerApi from '../../apis/trainerProfile';
import TrainerUpdateForm from '../../components/admin/Trainer/TrainerUpdateForm';

const UpdateContainer = () => {

  const navigate = useNavigate();

  const onUpdate = async (formData, headers) => {
    try {
      console.log('전달된 formData:', formData, headers);
  
      const response = await trainerApi.update(formData, headers);
      console.log('수정 성공:', response);
  
      alert('트레이너가 성공적으로 수정되었습니다!');
      navigate('/admin/ticket/ticketList');
  
    } catch (error) {
      console.error('수정 실패:', error);
      alert('수정에 실패했습니다. 다시 시도해주세요.');
    }
  };
  
  

  return (
    <TrainerUpdateForm onUpdate={onUpdate} />
  );
};

export default UpdateContainer;
