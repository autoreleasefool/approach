import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import FormFeature
import Foundation
import ModelsLibrary
import StringsLibrary

public typealias BowlerForm = Form<Bowler.Create, Bowler.Edit>

@Reducer
public struct BowlerEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var name: String
		public let kind: Bowler.Kind

		public let initialValue: BowlerForm.Value
		public var form: BowlerForm.State

		public init(value: BowlerForm.Value) {
			let isCreatingOpponent = (value.record as? Bowler.Create)?.kind == .opponent
			self.kind = isCreatingOpponent ? .opponent : .playable
			self.initialValue = value
			self.form = .init(
				initialValue: value,
				modelName: isCreatingOpponent ? Strings.Opponent.title : Bowler.Create.modelName
			)

			switch value {
			case let .create(new):
				self.name = new.name
			case let .edit(existing):
				self.name = existing.name
			}
		}

		mutating func syncFormSharedState() {
			switch form.initialValue {
			case var .create(new):
				new.name = name
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				form.value = .edit(existing)
			}
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case onAppear
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case form(BowlerForm.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.form, action: \.internal.form) {
			BowlerForm()
				.dependency(RecordPersistence(
					create: bowlers.create,
					update: bowlers.update,
					delete: { _ in },
					archive: bowlers.archive
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state.form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state.form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didArchive(result):
						return state.form.didFinishArchiving(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard, .didFinishArchiving, .didDelete:
						return .run { _ in await dismiss() }
					}

				case .form(.view), .form(.internal):
					return .none
				}

			case .binding:
				state.syncFormSharedState()
				return .none

			case .delegate:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { state, action in
			switch action {
			case .internal(.form(.delegate(.didFinishCreating))):
				return Analytics.Bowler.Created(kind: state.kind.rawValue)
			case .internal(.form(.delegate(.didFinishUpdating))):
				return Analytics.Bowler.Updated(kind: state.kind.rawValue)
			case .internal(.form(.delegate(.didFinishArchiving))):
				return Analytics.Bowler.Archived(kind: state.kind.rawValue)
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

extension Bowler.Create: CreateableRecord {
	public static let modelName = Strings.Bowler.title

	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension Bowler.Edit: EditableRecord {
	public var isDeleteable: Bool { false }
	public var isArchivable: Bool { true }
	public var isSaveable: Bool {
		!name.isEmpty
	}
}
