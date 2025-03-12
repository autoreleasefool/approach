import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("FilterButton", .snapshots(record: .missing))
struct FilterButtonTests {

	@Test("Active filter button snapshot", .tags(.snapshot))
	@MainActor
	func snapshotActiveFilterButton() {
		let filterButton = FilterButton(isActive: true) { }
		assertSnapshot(of: filterButton, as: .image)
	}

	@Test("Inactive filter button snapshot", .tags(.snapshot))
	@MainActor
	func snapshotInactiveFilterButton() {
		let filterButton = FilterButton(isActive: false) { }
		assertSnapshot(of: filterButton, as: .image)
	}
}
