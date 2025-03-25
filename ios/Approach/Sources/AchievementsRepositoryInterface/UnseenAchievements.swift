//
//  UnseenAchievements.swift
//  Approach
//
//  Created by Joseph Roque on 2025-03-24.
//

import PreferenceServiceInterface
import Sharing

extension SharedReaderKey where Self == AppStorageKey<Int> {
	public static var unseenAchievements: Self {
		appStorage(PreferenceKey.unseenAchievements.rawValue)
	}
}
