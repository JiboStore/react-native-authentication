import DeviceInfo from 'react-native-device-info';

let DeviceInfoUtil = {
  getDeviceInfo: () => {
    let jsonObj = {
      deviceId: DeviceInfo.getUniqueID(),
      deviceManufacturer: DeviceInfo.getManufacturer(),
      deviceModel: DeviceInfo.getModel(),
      deviceName: DeviceInfo.getSystemName(),
      deviceVersion: DeviceInfo.getSystemVersion(),
      appId: DeviceInfo.getBundleId(),
      appBuild: DeviceInfo.getBuildNumber(),
      appVersion: DeviceInfo.getReadableVersion()
    }
    return jsonObj;
  }
}

export default DeviceInfoUtil;