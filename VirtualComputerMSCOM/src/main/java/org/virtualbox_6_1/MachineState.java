package org.virtualbox_6_1;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MachineState {
	Null(0),
	PoweredOff(1),
	Saved(2),
	Teleported(3),
	Aborted(4),
	FirstOnline(5),
	Running(5),
	Paused(6),
	Stuck(7),
	FirstTransient(8),
	Teleporting(8),
	LiveSnapshotting(9),
	Starting(10), // 0x0000000A
	Stopping(11), // 0x0000000B
	Saving(12), // 0x0000000C
	Restoring(13), // 0x0000000D
	TeleportingPausedVM(14), // 0x0000000E
	TeleportingIn(15), // 0x0000000F
	DeletingSnapshotOnline(16), // 0x00000010
	DeletingSnapshotPaused(17), // 0x00000011
	LastOnline(18), // 0x00000012
	OnlineSnapshotting(18), // 0x00000012
	RestoringSnapshot(19), // 0x00000013
	DeletingSnapshot(20), // 0x00000014
	SettingUp(21), // 0x00000015
	LastTransient(22), // 0x00000016
	Snapshotting(22); // 0x00000016

	private final int value;

	public int value() {
		return value;
	}
}
