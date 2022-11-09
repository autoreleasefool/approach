import GRDB

struct ModelPersistence {
	let writer: any DatabaseWriter

	@Sendable func create<Model: PersistableRecord>(model: Model) async throws -> Void {
		try await writer.write {
			try model.insert($0)
		}
	}

	@Sendable func update<Model: PersistableRecord>(model: Model) async throws -> Void {
		try await writer.write {
			try model.update($0)
		}
	}

	@Sendable func delete<Model: PersistableRecord>(model: Model) async throws -> Void {
		_ = try await writer.write {
			try model.delete($0)
		}
	}
}
