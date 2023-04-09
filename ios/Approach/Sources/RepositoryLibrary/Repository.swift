import AsyncAlgorithms
import Foundation
import SortingLibrary

public func sort<T: Identifiable>(
	_ itemsStream: AsyncStream<[T]>,
	byIds idsStream: AsyncStream<[UUID]>
) -> AsyncStream<[T]> where T.ID == UUID {
	.init { continuation in
		let task = Task {
			for await (items, ids) in combineLatest(
				itemsStream,
				idsStream
			) {
				continuation.yield(items.sortBy(ids: ids))
			}
		}

		continuation.onTermination = { _ in task.cancel() }
	}
}

public func sort<T: Identifiable>(
	_ itemsStream: AsyncThrowingStream<[T], Error>,
	byIds idsStream: AsyncStream<[UUID]>
) -> AsyncThrowingStream<[T], Error> where T.ID == UUID {
	.init { continuation in
		let task = Task {
			do {
				for try await (items, ids) in combineLatest(
					itemsStream,
					idsStream
				) {
					continuation.yield(items.sortBy(ids: ids))
				}
			} catch {
				continuation.finish(throwing: error)
			}
		}

		continuation.onTermination = { _ in task.cancel() }
	}
}
