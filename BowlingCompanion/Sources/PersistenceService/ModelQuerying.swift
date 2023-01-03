import GRDB

struct ModelQuerying {
	let reader: DatabaseReader

	@Sendable func fetchOne<Model, Request: SingleQueryable>(
		request: Request
	) async throws -> Model? where Request.Model == Model {
		try await reader.read(request.fetchValue(_:))
	}

	@Sendable func fetchAll<Model, Request: ManyQueryable>(
		request: Request
	) async throws -> [Model] where Request.Model == Model {
		try await reader.read(request.fetchValues(_:))
	}

	@Sendable func observeAll<Model, Request: ManyQueryable>(
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
