import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import ModelsLibrary

extension AndroidApproachV5SQLiteImporter {
	struct GearImportStep: SQLiteImportStep {
		func performImport(from importDb: Database, to exportDb: Database) throws {
			@Dependency(\.uuid) var uuid

			let gearRows = try Row.fetchCursor(
				importDb,
				sql: "SELECT id, name, kind, avatar, owner_id FROM gear;"
			)

			while let gearRow = try gearRows.next() {
				let id: Gear.ID = gearRow["id"]
				let name: String = gearRow["name"]
				let kind: String = gearRow["kind"]
				let avatarStr: String = gearRow["avatar"]
				let ownerId: Bowler.ID? = gearRow["owner_id"]

				let avatar = parseAvatar(from: avatarStr)
				try avatar.insert(exportDb)

				let gear = Gear.Database(
					id: id,
					name: name,
					kind: Gear.Kind(rawValue: kind.snakeCaseToCamelCase) ?? .other,
					bowlerId: ownerId,
					avatarId: avatar.id
				)

				try gear.insert(exportDb)
			}
		}

		private func parseAvatar(from str: String) -> Avatar.Database {
			@Dependency(\.uuid) var uuid

			let components = str.components(separatedBy: ";")
			guard components.count == 3 else { return Avatar.Database.undefined() }

			let text = components[0]
			guard let rgb1 = parseRGB(from: components[1]), let rgb2 = parseRGB(from: components[2]) else {
				return Avatar.Database.undefined()
			}

			return Avatar.Database(
				id: uuid(),
				value: .text(
					text,
					.gradient(
						Avatar.Background.RGB(rgb1.red, rgb1.green, rgb1.blue),
						Avatar.Background.RGB(rgb2.red, rgb2.green, rgb2.blue)
					)
				)
			)

		}

		private func parseRGB(from str: String) -> (red: CGFloat, green: CGFloat, blue: CGFloat)? {
			let components = str.components(separatedBy: ",")
			let intComponents = components.compactMap { Int($0) }
			guard intComponents.count == 3 else { return nil }

			return (
				CGFloat(intComponents[0]) / 255.0,
				CGFloat(intComponents[1]) / 255.0,
				CGFloat(intComponents[2]) / 255.0
			)
		}
	}
}

extension Avatar.Database {
	fileprivate static func undefined() -> Avatar.Database {
		@Dependency(\.uuid) var uuid
		return Avatar.Database(id: uuid(), value: .text("NA", .default))
	}
}
