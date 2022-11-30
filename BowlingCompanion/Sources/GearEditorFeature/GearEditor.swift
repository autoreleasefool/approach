import BaseFormFeature
import BowlersDataProviderInterface
import ComposableArchitecture
import Foundation
import ResourcePickerFeature
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary

extension Gear: BaseFormModel {
	static public var modelName = Strings.Gear.Model.name
}

extension Bowler: PickableResource {
	static public var pickableModelName = Strings.Bowlers.Model.name
	public var pickableTitle: String { name }
	public var pickableSubtitle: String? { nil }
}

public struct GearEditor: ReducerProtocol {
	public typealias Form = BaseForm<Gear, Fields>

	public struct Fields: BaseFormState, Equatable {
		@BindableState public var name = ""
		@BindableState public var kind: Gear.Kind = .bowlingBall
		public var bowlerPicker: ResourcePicker<Bowler>.State = .init(selected: [], limit: 1)

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
		}
	}

	public struct State: Equatable {
		public var base: Form.State
		public var initialBowler: Bowler?
		public var isBowlerPickerPresented = false

		public init(mode: Form.Mode) {
			var fields = Fields()
			if case let .edit(gear) = mode {
				fields.name = gear.name
				fields.kind = gear.kind
			}

			fields.bowlerPicker = .init(selected: Set([mode.model?.bowler].compactMap({ $0 })), limit: 1)
			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: BindableAction, Equatable {
		case loadInitialData
		case bowlerResponse(TaskResult<Bowler?>)
		case binding(BindingAction<State>)
		case form(Form.Action)
		case bowlerPicker(ResourcePicker<Bowler>.Action)
		case setBowlerPickerSheet(isPresented: Bool)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider
	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createGear,
					update: persistenceService.updateGear,
					delete: persistenceService.deleteGear
				))
		}

		Scope(state: \.base.form.bowlerPicker, action: /Action.bowlerPicker) {
			ResourcePicker { try await bowlersDataProvider.fetchBowlers(.init(ordering: .byName)) }
		}

		Reduce { state, action in
			switch action {
			case .loadInitialData:
				if case let .edit(gear) = state.base.mode, let bowlerId = gear.bowler {
					return .task {
						await .bowlerResponse(TaskResult {
							let bowlers = try await bowlersDataProvider.fetchBowlers(.init(filter: [.id(bowlerId)], ordering: .byName))
							return bowlers.first
						})
					}
				}
				return .none

			case let .bowlerResponse(.success(bowler)):
				state.initialBowler = bowler
				return .none

			case .bowlerResponse(.failure):
				// TODO: handle error failing to load bowler
				return .none

			case let .setBowlerPickerSheet(isPresented):
				state.isBowlerPickerPresented = isPresented
				return .none

			case .bowlerPicker(.saveButtonTapped), .bowlerPicker(.cancelButtonTapped):
				state.isBowlerPickerPresented = false
				return .none

			case .binding, .form, .bowlerPicker:
				return .none
			}
		}
	}
}

extension GearEditor.Fields {
	public func model(fromExisting existing: Gear?) -> Gear {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		return .init(
			bowler: bowlerPicker.selected.first,
			id: existing?.id ?? uuid(),
			name: name,
			kind: kind
		)
	}
}
