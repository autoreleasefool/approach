import Foundation
import SwiftUI

public enum Announcement: String, CaseIterable {
	case christmas2023

	public func meetsExpectationsToShow() -> Bool {
		switch self {
		case .christmas2023: Christmas2023AnnouncementView.meetsExpectationsToShow()
		}
	}
}
