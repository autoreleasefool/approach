import SnapshotTesting
import SwiftUI
import XCTest
@testable import ViewsLibrary

final class AddButtonTests: XCTestCase {
	func testAddButtonSnapshot() {
		let addButton = AddButton { }

		let vc = UIHostingController(rootView: addButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
