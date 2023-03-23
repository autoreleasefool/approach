import GRDB
import SharedModelsPersistableLibrary

struct ModelPersistence {
	let writer: any DatabaseWriter

	@Sendable func save<Model: PersistableRecord>(model: Model) async throws {
		try await writer.write { try model.save($0) }
	}

	@Sendable func save<Model: PersistableRecord>(models: [Model]) async throws {
		try await writer.write {
			for model in models {
				try model.save($0)
			}
		}
	}

	@Sendable func save<Model: PersistableSQL>(model: Model) async throws {
		try await writer.write {
			try model.save($0)
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
