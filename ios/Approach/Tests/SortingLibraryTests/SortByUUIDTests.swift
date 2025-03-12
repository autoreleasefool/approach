import Foundation
import SortingLibrary
import Testing
import TestUtilitiesLibrary

@Suite("SortByUUID", .tags(.library))
struct SortByUUIDTests {

	@Test("Sort with no IDs does not change order", .tags(.unit))
	func sortWithNoIDsDoesNotChangeOrder() {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let sortables = [
			Sortable(id: id0),
			Sortable(id: id2),
			Sortable(id: id1),
		]

		#expect(sortables == sortables.sortBy(ids: []))
	}

	@Test("Sorts all elements", .tags(.unit))
	func sortsAllElements() {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let sortables = [
			Sortable(id: id0),
			Sortable(id: id2),
			Sortable(id: id1),
		]

		let expectedOrder = [
			Sortable(id: id2),
			Sortable(id: id1),
			Sortable(id: id0),
		]

		#expect(expectedOrder == sortables.sortBy(ids: [id2, id1, id0]))
	}

	@Test("Sorts elements with IDs to start of list", .tags(.unit))
	func sortsElementsWithIDsToStartOfList() {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let sortables = [
			Sortable(id: id0),
			Sortable(id: id2),
			Sortable(id: id1),
		]

		let expectedOrder = [
			Sortable(id: id1),
			Sortable(id: id0),
			Sortable(id: id2),
		]

		#expect(expectedOrder == sortables.sortBy(ids: [id1]))
	}
}

struct Sortable: Equatable, Identifiable {
	let id: UUID
}
