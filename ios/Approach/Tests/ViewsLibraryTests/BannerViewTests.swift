import SnapshotTesting
import SwiftUI
@testable import ViewsLibrary
import XCTest

final class BannerViewTests: XCTestCase {
	func testBannerSnapshot() {
		let badges = VStack {
			VStack {
				Banner("Banner", style: .plain)
				Banner("Banner", style: .success)
				Banner("Banner", style: .destructive)
				Banner("Banner", style: .info)
				Banner("Banner", style: .primary)
				Banner("Banner", style: .init(foreground: .red, background: .blue))
			}

			VStack {
				Banner("Banner", message: "This is a message", style: .plain)
				Banner("Banner", message: "This is a message", style: .success)
				Banner("Banner", message: "This is a message", style: .destructive)
				Banner("Banner", message: "This is a message", style: .info)
				Banner("Banner", message: "This is a message", style: .primary)
				Banner("Banner", message: "This is a message", style: .init(foreground: .red, background: .blue))
			}
		}

		let vc = UIHostingController(rootView: badges)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
