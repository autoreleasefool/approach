import BaseFormLibrary
import BowlersDataProviderInterface
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import Foundation
import PersistenceServiceInterface
import ResourcePickerLibrary
import SharedModelsFetchableLibrary
import SharedModelsLibrary
import StringsLibrary

extension Gear: BaseFormModel {
	static public var modelName = Strings.Gear.title
}

extension Bowler: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Bowler.title : Strings.Bowler.List.title
	}
}

public struct GearEditor: Reducer {
	public typealias Form = BaseForm<Gear, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var bowlerPicker: ResourcePicker<Bowler, Bowler.FetchRequest>.State
		@BindingState public var name = ""
		@BindingState public var kind: Gear.Kind = .bowlingBall

		init(bowler: Bowler.ID?) {
			self.bowlerPicker = .init(
				selected: Set([bowler].compactMap({ $0 })),
				query: .init(filter: nil, ordering: .byName),
				limit: 1,
				showsCancelHeaderButton: false
			)
		}

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
		}
	}

	public struct State: Equatable {
		public var base: Form.State
		public var initialBowler: Bowler?
		public var isBowlerPickerPresented = false
		public let hasAvatarsEnabled: Bool

		public init(mode: Form.Mode) {
			var fields: Fields
			switch mode {
			case let .edit(gear):
				fields = Fields(bowler: gear.bowler)
				fields.name = gear.name
				fields.kind = gear.kind
			case .create:
				fields = Fields(bowler: nil)
			}

			self.base = .init(mode: mode, form: fields)

			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.hasAvatarsEnabled = featureFlags.isEnabled(.avatars)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case setBowlerPicker(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case didLoadBowler(TaskResult<Bowler?>)
			case form(Form.Action)
			case bowlerPicker(ResourcePicker<Bowler, Bowler.FetchRequest>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider
	@Dependency(\.persistenceService) var persistenceService

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.internal..Action.InternalAction.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					save: persistenceService.saveGear,
					delete: persistenceService.deleteGear
				))
		}

		Scope(state: \.base.form.bowlerPicker, action: /Action.internal..Action.InternalAction.bowlerPicker) {
			ResourcePicker(observeResources: bowlersDataProvider.observeBowlers)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					if let bowler = state.base.form.bowlerPicker.selected.first {
						return .run { send in
							for try await bowler in bowlersDataProvider.observeBowler(.init(filter: .id(bowler))) {
								await send(.internal(.didLoadBowler(.success(bowler))))
							}
						} catch: { error, send in
							await send(.internal(.didLoadBowler(.failure(error))))
						}
					}
					return .none

				case let .setBowlerPicker(isPresented):
					state.isBowlerPickerPresented = isPresented
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadBowler(.success(bowler)):
					state.initialBowler = bowler
					return .none

				case .didLoadBowler(.failure):
					// TODO: handle error failing to load bowler
					return .none

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didSaveModel(gear):
						return .task { .internal(.form(.callback(.didFinishSaving(.success(gear))))) }

					case let .didDeleteModel(gear):
						return .task { .internal(.form(.callback(.didFinishDeleting(.success(gear))))) }

					case .didFinishSaving, .didFinishDeleting, .didDiscard:
						return .task { .delegate(.didFinishEditing) }
					}

				case let .bowlerPicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.isBowlerPickerPresented = false
						return .none
					}

				case .bowlerPicker(.view), .bowlerPicker(.internal), .form(.view), .form(.internal), .form(.callback):
					return .none
				}

			case .binding, .delegate:
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
