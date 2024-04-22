import SnapshotTesting
import SwiftUI
@testable import ViewsLibrary
import XCTest

final class ChipTests: XCTestCase {
	func testChipSnapshot() {
		let chips = VStack {
			Chip(title: "Chip")
			Chip(title: "Chip", icon: .person)
			Chip(title: "Radio Box", icon: .star, accessory: .radioBox)
			Chip(title: "Radio Box Selected", icon: .star, accessory: .radioBoxSelected)
			Chip(title: "Info", icon: .star, style: .info)
			Chip(title: "Plain", icon: .star, style: .plain)
			Chip(title: "Primary", icon: .star, style: .primary)
		}

		let vc = UIHostingController(rootView: chips)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhoneSe))
	}
}
