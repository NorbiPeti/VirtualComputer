package org.virtualbox_6_1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VBoxEventType {
	Invalid(0),
	Any(1),
	Vetoable(2),
	MachineEvent(3),
	SnapshotEvent(4),
	InputEvent(5),
	LastWildcard(31), // 0x0000001F
	OnMachineStateChanged(32), // 0x00000020
	OnMachineDataChanged(33), // 0x00000021
	OnExtraDataChanged(34), // 0x00000022
	OnExtraDataCanChange(35), // 0x00000023
	OnMediumRegistered(36), // 0x00000024
	OnMachineRegistered(37), // 0x00000025
	OnSessionStateChanged(38), // 0x00000026
	OnSnapshotTaken(39), // 0x00000027
	OnSnapshotDeleted(40), // 0x00000028
	OnSnapshotChanged(41), // 0x00000029
	OnGuestPropertyChanged(42), // 0x0000002A
	OnMousePointerShapeChanged(43), // 0x0000002B
	OnMouseCapabilityChanged(44), // 0x0000002C
	OnKeyboardLedsChanged(45), // 0x0000002D
	OnStateChanged(46), // 0x0000002E
	OnAdditionsStateChanged(47), // 0x0000002F
	OnNetworkAdapterChanged(48), // 0x00000030
	OnSerialPortChanged(49), // 0x00000031
	OnParallelPortChanged(50), // 0x00000032
	OnStorageControllerChanged(51), // 0x00000033
	OnMediumChanged(52), // 0x00000034
	OnVRDEServerChanged(53), // 0x00000035
	OnUSBControllerChanged(54), // 0x00000036
	OnUSBDeviceStateChanged(55), // 0x00000037
	OnSharedFolderChanged(56), // 0x00000038
	OnRuntimeError(57), // 0x00000039
	OnCanShowWindow(58), // 0x0000003A
	OnShowWindow(59), // 0x0000003B
	OnCPUChanged(60), // 0x0000003C
	OnVRDEServerInfoChanged(61), // 0x0000003D
	OnEventSourceChanged(62), // 0x0000003E
	OnCPUExecutionCapChanged(63), // 0x0000003F
	OnGuestKeyboard(64), // 0x00000040
	OnGuestMouse(65), // 0x00000041
	OnNATRedirect(66), // 0x00000042
	OnHostPCIDevicePlug(67), // 0x00000043
	OnVBoxSVCAvailabilityChanged(68), // 0x00000044
	OnBandwidthGroupChanged(69), // 0x00000045
	OnGuestMonitorChanged(70), // 0x00000046
	OnStorageDeviceChanged(71), // 0x00000047
	OnClipboardModeChanged(72), // 0x00000048
	OnDnDModeChanged(73), // 0x00000049
	OnNATNetworkChanged(74), // 0x0000004A
	OnNATNetworkStartStop(75), // 0x0000004B
	OnNATNetworkAlter(76), // 0x0000004C
	OnNATNetworkCreationDeletion(77), // 0x0000004D
	OnNATNetworkSetting(78), // 0x0000004E
	OnNATNetworkPortForward(79), // 0x0000004F
	OnGuestSessionStateChanged(80), // 0x00000050
	OnGuestSessionRegistered(81), // 0x00000051
	OnGuestProcessRegistered(82), // 0x00000052
	OnGuestProcessStateChanged(83), // 0x00000053
	OnGuestProcessInputNotify(84), // 0x00000054
	OnGuestProcessOutput(85), // 0x00000055
	OnGuestFileRegistered(86), // 0x00000056
	OnGuestFileStateChanged(87), // 0x00000057
	OnGuestFileOffsetChanged(88), // 0x00000058
	OnGuestFileRead(89), // 0x00000059
	OnGuestFileWrite(90), // 0x0000005A
	OnRecordingChanged(91), // 0x0000005B
	OnGuestUserStateChanged(92), // 0x0000005C
	OnGuestMultiTouch(93), // 0x0000005D
	OnHostNameResolutionConfigurationChange(94), // 0x0000005E
	OnSnapshotRestored(95), // 0x0000005F
	OnMediumConfigChanged(96), // 0x00000060
	OnAudioAdapterChanged(97), // 0x00000061
	OnProgressPercentageChanged(98), // 0x00000062
	OnProgressTaskCompleted(99), // 0x00000063
	OnCursorPositionChanged(100), // 0x00000064
	OnGuestAdditionsStatusChanged(101), // 0x00000065
	OnGuestMonitorInfoChanged(102), // 0x00000066
	OnGuestFileSizeChanged(103), // 0x00000067
	OnClipboardFileTransferModeChanged(104), // 0x00000068
	Last(105); // 0x00000069

	private final int value;
	public final int value() {
		return value;
	}
}
