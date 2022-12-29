import SnapshotTesting
import SwiftUI
import XCTest
@testable import ViewsLibrary

final class BadgeViewTests: XCTestCase {
	func testBadgeViewSnapshot() {
		let badges = VStack {
			BadgeView("Badge", style: .plain)
			BadgeView("Badge", style: .success)
			BadgeView("Badge", style: .destructive)
			BadgeView("Badge", style: .info)
			BadgeView("Badge", style: .primary) // FIXME: app colors not rendering
			BadgeView("Badge", style: .custom(foreground: .red, background: .blue))
		}


		let vc = UIHostingController(rootView: badges)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
