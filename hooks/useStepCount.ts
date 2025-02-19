import React, {useCallback, useEffect, useState} from 'react';
import StepCountModule from '../modules/StepCountModule';

const useStepCount = () => {
  const [errors, setErrors] = useState('');
  const [stepCount, setStepCount] = useState(0);

  useEffect(() => {
    StepCountModule.startStepUpdates()
      .then(data => setStepCount(data.steps))
      .catch(error => setErrors(error));
    return () => StepCountModule.stopStepUpdates();
  }, []);

  const refetch = useCallback(() => {
    StepCountModule.getStepCount().then(data => setStepCount(data.steps));
  }, []);

  return {
    stepCount,
    refetch,
    errors,
  };
};

export default useStepCount;
