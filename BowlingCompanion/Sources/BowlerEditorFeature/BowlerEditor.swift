import BaseFormFeature
import BowlersDataProviderInterface
import ComposableArchitecture
import Foundation
import SharedModelsLibrary

extension Bowler: BaseFormModel {
	static public var modelName = "Bowler"
}

public struct BowlerEditor: ReducerProtocol {
	public typealias Form = BaseForm<Bowler, Fields>

	public struct Fields: BaseFormState, Equatable {
		@BindableState public var name = ""

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
		}
	}

	public struct State: Equatable {
		public var base: Form.State

		public init(mode: Form.Mode) {
			var fields = Fields()
			if case let .edit(bowler) = mode {
				fields.name = bowler.name
			}

			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
		case form(Form.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider

	var bowlerFormService: FormModelService {
		.init(
			create: { model in
				guard let bowler = model as? Bowler else { return }
				try await bowlersDataProvider.create(bowler)
			},
			update: { model in
				guard let bowler = model as? Bowler else { return }
				try await bowlersDataProvider.update(bowler)
			},
			delete: { model in
				guard let bowler = model as? Bowler else { return }
				try await bowlersDataProvider.delete(bowler)
			}
		)
	}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.formModelService, bowlerFormService)
		}

		Reduce { _, action in
			switch action {
			case .binding, .form:
				return .none
			}
		}
	}
}

extension BowlerEditor.Fields {
	public func model(fromExisting existing: Bowler?) -> Bowler {
		@Dependency(\.uuid) var uuid: UUIDGenerator
		@Dependency(\.date) var date: DateGenerator

		return .init(
			id: existing?.id ?? uuid(),
			name: name,
			createdAt: existing?.createdAt ?? date(),
			lastModifiedAt: date()
		)
	}
}
