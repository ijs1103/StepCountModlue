import React, {useEffect} from 'react';
import {Alert, SafeAreaView, Text} from 'react-native';
import useStepCount from './hooks/useStepCount';

function App(): React.JSX.Element {
  const {stepCount, refetch, errors} = useStepCount();

  return (
    <SafeAreaView
      style={{
        flex: 1,
        backgroundColor: '#fff',
        justifyContent: 'center',
        alignItems: 'center',
      }}>
      <Text
        style={{fontSize: 20, color: '#000'}}>{`걸음수: ${stepCount}`}</Text>
    </SafeAreaView>
  );
}

export default App;
