import AssetsLibrary
@testable import ListContentLibrary
import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary

@Suite("ListContent", .snapshots(record: .missing))
struct ListContentTests {
	@Test("List empty content snapshot", .tags(.snapshot))
	@MainActor
	func snapshotEmptyContent() {
		let emptyContent = ListEmptyContent(
			Asset.Media.EmptyState.bowlers,
			title: "A Relevant Title",
			message: "An Irrelevant Message",
			style: .empty
		) {
			EmptyContentAction(title: "Action") { }
		}
		.frame(width: 428, height: 926)

		assertSnapshot(of: emptyContent, as: .image)
	}

	@Test("List error content snapshot", .tags(.snapshot))
	@MainActor
	func snapshotErrorContent() {
		let errorContent = ListEmptyContent(
			Asset.Media.Error.notFound,
			title: "A Relevant Title",
			message: "An Irrelevant Message",
			style: .error
		) {
			EmptyContentAction(title: "Action") { }
		}
		.frame(width: 428, height: 926)

		assertSnapshot(of: errorContent, as: .image)
	}
}
