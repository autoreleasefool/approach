import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("ListProgressView", .tags(.library), .snapshots(record: .failed))
struct ListProgressViewTests {

	@Test("ListProgressView snapshots", .tags(.snapshot))
	@MainActor
	func snapshotListProgressView() {
		let listProgressView = List {
			ListProgressView()
		}
			.frame(width: 428, height: 500)

		assertSnapshot(of: listProgressView, as: .image)
	}
}
