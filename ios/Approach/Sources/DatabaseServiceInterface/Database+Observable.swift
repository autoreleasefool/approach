import GRDB

extension DatabaseReader {
	public func observe<Model>(
		_ fetch: @escaping (Database) throws -> [Model]
	) -> AsyncThrowingStream<[Model], Error> {
		.init { continuation in
			let task = Task {
				do {
					let observation = ValueObservation.tracking(fetch)

					for try await value in observation.values(in: self) {
						continuation.yield(value)
					}
				} catch {
					continuation.finish(throwing: error)
				}
			}
			continuation.onTermination = { _ in task.cancel() }
		}
	}

	public func observeOne<Model: Identifiable>(
		_ fetch: @escaping (Database) throws -> Model?
	) -> AsyncThrowingStream<Model, Error> {
		.init { continuation in
			let task = Task {
				do {
					let observation = ValueObservation.tracking(fetch)

					for try await value in observation.values(in: self) {
						if let value {
							continuation.yield(value)
						} else {
							continuation.finish()
							break
						}
					}
				} catch {
					continuation.finish(throwing: error)
				}
			}
			continuation.onTermination = { _ in task.cancel() }
		}
	}
}
