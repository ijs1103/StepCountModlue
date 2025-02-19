import {NativeModules} from 'react-native';
const {StepCountModule} = NativeModules;

interface IStepCountModule {
  isStepCountingAvailable: () => Promise<boolean>;
  getStepCount: () => Promise<{steps: number}>;
  startStepUpdates: () => Promise<{steps: number}>;
  stopStepUpdates: () => void;
}

export default StepCountModule as IStepCountModule;
