import SnapshotTesting
import SwiftUI
import XCTest
@testable import ViewsLibrary

final class DeleteButtonTests: XCTestCase {
	func testDeleteButtonSnapshot() {
		let deleteButton = DeleteButton { }

		let vc = UIHostingController(rootView: deleteButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
