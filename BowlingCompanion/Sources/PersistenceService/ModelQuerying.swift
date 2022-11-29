import GRDB

struct ModelQuerying {
	let reader: DatabaseReader

	@Sendable func fetchAll<Model: FetchableRecord, Request: Queryable>(
		request: Request
	) async throws -> [Model] where Request.Model == Model {
		try await reader.read(request.fetchValues(_:))
	}

	@Sendable func observeAll<Model: FetchableRecord, Request: Queryable>(
		request: Request
	) -> AsyncThrowingStream<[Model], Error> where Request.Model == Model {
		return .init { continuation in
			Task {
				do {
					let observation = ValueObservation.tracking(request.fetchValues(_:))

					for try await values in observation.values(in: reader) {
						continuation.yield(values)
					}

					continuation.finish()
				} catch {
					continuation.finish(throwing: error)
				}
			}
		}
	}
}
