import GRDB

struct ModelFetching {
	let reader: DatabaseReader

	@Sendable func fetchAll<Model: FetchableRecord, Request: Fetchable>(
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
