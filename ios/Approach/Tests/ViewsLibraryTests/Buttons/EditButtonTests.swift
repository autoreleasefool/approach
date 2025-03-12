import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("EditButton", .snapshots(record: .missing))
struct EditButtonTests {

	@Test("Edit button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotEditButton() {
		let editButton = EditButton { }
		assertSnapshot(of: editButton, as: .image)
	}
}
