import SnapshotTesting
import SwiftUI
import XCTest
@testable import ViewsLibrary

final class FilterButtonTests: XCTestCase {
	func testActiveFilterButtonSnapshot() {
		let filterButton = FilterButton(isActive: true) { }

		let vc = UIHostingController(rootView: filterButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}

	func testInactiveFilterButtonSnapshot() {
		let filterButton = FilterButton(isActive: false) { }

		let vc = UIHostingController(rootView: filterButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
