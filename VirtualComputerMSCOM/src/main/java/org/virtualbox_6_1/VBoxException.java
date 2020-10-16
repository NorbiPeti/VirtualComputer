package org.virtualbox_6_1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VBoxException extends RuntimeException {
	private final long resultCode;
}
