import React, {useCallback, useEffect, useState} from 'react';
import StepCountModule from '../modules/StepCountModule';
import {DeviceEventEmitter} from 'react-native';

const useStepCount = () => {
  const [errors, setErrors] = useState('');
  const [stepCount, setStepCount] = useState(0);

  useEffect(() => {
    StepCountModule.startStepUpdates()
      .then(data => setStepCount(data.steps))
      .catch(error => setErrors(error));
    const subscription = DeviceEventEmitter.addListener(
      'StepCountUpdated',
      stepCount => {
        setStepCount(stepCount);
      },
    );

    return () => {
      subscription.remove();
      StepCountModule.stopStepUpdates();
    };
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
