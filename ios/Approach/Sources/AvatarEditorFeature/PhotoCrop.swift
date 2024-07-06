import ComposableArchitecture
import FeatureActionLibrary
import SwiftUI

@Reducer
public struct PhotoCrop: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var image: UIImage
		public var offset: CGSize = .zero

		public init(image: UIImage) {
			self.image = image
		}
	}

	public enum Action: ViewAction, FeatureAction, BindableAction {
		@CasePathable public enum View {
			case didTapDone
		}

		@CasePathable public enum Delegate {
			case didFinishCropping(UIImage)
		}

		@CasePathable public enum Internal {
			case doNothing
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapDone:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .doNothing:
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
	}
}

@ViewAction(for: PhotoCrop.self)
public struct PhotoCropView: View {
	@Bindable public var store: StoreOf<PhotoCrop>

	public init(store: StoreOf<PhotoCrop>) {
		self.store = store
	}

	public var body: some View {
		ZStack {
			Image(uiImage: store.image)
				.resizable()
				.scaledToFit()
		}
	}
}
