import GRDB
import SharedModelsPersistableLibrary

struct ModelPersistence {
	let writer: any DatabaseWriter

	@Sendable func create<Model: PersistableRecord>(model: Model) async throws {
		try await writer.write {
			try model.insert($0)
		}
	}

	@Sendable func create<Model: PersistableRecord>(models: [Model]) async throws {
		try await writer.write {
			for model in models {
				try model.insert($0)
			}
		}
	}

	@Sendable func update<Model: PersistableRecord>(model: Model) async throws {
		try await writer.write {
			try model.update($0)
		}
	}

	@Sendable func update<Model: PersistableRecord>(models: [Model]) async throws {
		try await writer.write {
			for model in models {
				try model.update($0)
			}
		}
	}

	@Sendable func update<Model: PersistableSQL>(model: Model) async throws {
		try await writer.write {
			try model.update($0)
		}
	}

	@Sendable func delete<Model: PersistableRecord>(model: Model) async throws {
		_ = try await writer.write {
			try model.delete($0)
		}
	}

	@Sendable func delete<Model: PersistableRecord>(models: [Model]) async throws {
		_ = try await writer.write {
			for model in models {
				try model.delete($0)
			}
		}
	}
}
