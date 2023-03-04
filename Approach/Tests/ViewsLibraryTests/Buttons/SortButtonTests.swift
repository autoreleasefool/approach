import SnapshotTesting
import SwiftUI
import XCTest
@testable import ViewsLibrary

final class SortButtonTests: XCTestCase {
	func testActiveSortButtonSnapshot() {
		let sortButton = SortButton(isActive: true) { }

		let vc = UIHostingController(rootView: sortButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}

	func testInactiveSortButtonSnapshot() {
		let sortButton = SortButton(isActive: false) { }

		let vc = UIHostingController(rootView: sortButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
