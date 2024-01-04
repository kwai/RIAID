Pod::Spec.new do |s|
    s.name             = 'RIAIDEngine'
    s.version          = '2.43.0'
    s.summary          = 'RIAID core implement'
    s.description      = <<-DESC
  RIAID 引擎实现，包括核心 Browser、Render 部分
                         DESC
  
    s.homepage         = 'TODO: you must set this homepage'
    s.license          = { :type => 'MIT', :file => 'LICENSE' }
    s.author           = { 'hufangzheng' => 'hufangzheng@kuaishou.com' }
    s.source           = {:http => 'https://cocoadepot.top/placeholder'}
  
    s.ios.deployment_target = '12.0'
    s.pod_target_xcconfig = { "GCC_PREPROCESSOR_DEFINITIONS" => "  'KSPOD_NAME=@\"#{s.name.to_s}\"'  "  }

    s.source_files = 'Pod/Classes/**/*'
    
    # third libs
    s.dependency 'YYText'
    s.dependency 'SDWebImageWebPCoder'
    s.dependency 'SSZipArchive'
    s.dependency 'Protobuf'

    non_arc_files = 'Pod/Classes/RIAIDFoundation/Json2Pb/*.{h,m}'
    protocol_files = 'Pod/Classes/RIAIDProtocol/*.{h,m}'
    lite_files = 'Pod/Classes/RIAIDLite/**/*'

    s.subspec 'Json2Pb' do |ss|
      ss.source_files = non_arc_files
      ss.requires_arc = false
      
      ss.pod_target_xcconfig = { 'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS=1' }
    end
    
    s.subspec 'Protocol' do |ss|
      ss.source_files = protocol_files
      ss.requires_arc = false

      ss.pod_target_xcconfig = { 'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS=1' }
    end

    s.subspec 'Core' do |ss|
      ss.source_files = lite_files
      ss.dependency 'RIAIDEngine/Protocol'
    end

  end
