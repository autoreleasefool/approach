import AssetsLibrary
import SnapshotTesting
import SwiftUI
@testable import ViewsLibrary
import XCTest

final class ListContentTests: XCTestCase {
	func testEmptyContentSnapshot() {
		let emptyContent = ListEmptyContent(
			Asset.Media.EmptyState.bowlers,
			title: "A Relevant Title",
			message: "An Irrelevant Message",
			style: .empty
		) {
			EmptyContentAction(title: "Action") { }
		}

		let vc = UIHostingController(rootView: emptyContent)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}

	func testEmptyErrorContentSnapshot() {
		let emptyContent = ListEmptyContent(
			Asset.Media.Error.notFound,
			title: "A Relevant Title",
			message: "An Irrelevant Message",
			style: .error
		) {
			EmptyContentAction(title: "Action") { }
		}

		let vc = UIHostingController(rootView: emptyContent)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}

	// TODO: ListContentSnapshotTest
}
