import SnapshotTesting
import SwiftUI
@testable import ViewsLibrary
import XCTest

final class AddButtonTests: XCTestCase {
	func testAddButtonSnapshot() {
		let addButton = AddButton { }

		let vc = UIHostingController(rootView: addButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
