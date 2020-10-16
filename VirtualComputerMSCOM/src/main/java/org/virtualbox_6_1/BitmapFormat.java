package org.virtualbox_6_1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BitmapFormat {
	Opaque(0),
	PNG(541544016), // 0x20474E50
	BGR(542263106), // 0x20524742
	BGR0(810698562), // 0x30524742
	RGBA(1094862674), // 0x41424752
	BGRA(1095911234), // 0x41524742
	JPEG(1195724874); // 0x4745504A

	@Getter
	private final int value;
}
