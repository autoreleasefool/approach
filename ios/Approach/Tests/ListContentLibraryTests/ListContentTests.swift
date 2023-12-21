import AssetsLibrary
@testable import ListContentLibrary
import SnapshotTesting
import SwiftUI
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

		assertSnapshot(matching: vc, as: .image(on: .iPhoneSe))
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

		assertSnapshot(matching: vc, as: .image(on: .iPhoneSe))
	}
}
