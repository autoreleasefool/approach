import SnapshotTesting
import SwiftUI
import XCTest
@testable import ViewsLibrary

final class EditButtonTests: XCTestCase {
	func testEditButtonSnapshot() {
		let editButton = EditButton { }
		let vc = UIHostingController(rootView: editButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
